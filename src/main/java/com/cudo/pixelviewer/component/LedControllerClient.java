package com.cudo.pixelviewer.component;

import com.cudo.pixelviewer.bo.mapper.LedconMapper;
import com.cudo.pixelviewer.bo.mapper.PwrconMapper;
import com.cudo.pixelviewer.vo.LedconVo;
import com.cudo.pixelviewer.vo.PwrconVo;
import com.cudo.pixelviewer.vo.ResponseWithIpVo;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Component
@Slf4j
public class LedControllerClient {

    private EventLoopGroup group;
    private TcpClientHandler tcpClientHandler;
    private Map<Channel, CompletableFuture<ResponseWithIpVo>> channelFutureMap;
    final LedconMapper ledconMapper;
    private Map<Channel, List<byte[]>> responseFragmentMap;
    private AtomicBoolean detectSenderMessage;

    private final static Integer EXPECTED_TOTAL_LENGTH = 260;
    private final static Integer SOCKET_RE_CONNECT_COUNT = 3;

    public LedControllerClient(final LedconMapper ledconMapper) {
        this.ledconMapper = ledconMapper;
    }


    @PostConstruct
    public void start() throws Exception {
        group = new NioEventLoopGroup();
        tcpClientHandler = new TcpClientHandler();
        channelFutureMap = new ConcurrentHashMap<>();
        responseFragmentMap = new ConcurrentHashMap<>();
        detectSenderMessage = new AtomicBoolean(false);

        connect(); // 초기 연결
    }

    public Map<Channel, CompletableFuture<ResponseWithIpVo>> getChannelFutureMap() {
        return this.channelFutureMap;
    }

    public void setChannelFutureMap(Map<Channel, CompletableFuture<ResponseWithIpVo>> channelFutureMap) {
        this.channelFutureMap = channelFutureMap;

    }

    private void connect() {
        List<LedconVo> ipPortList = ledconMapper.getLedconList();

        for (LedconVo ipPort : ipPortList) {
            String ip = ipPort.getIp();
            Integer port = ipPort.getPort();

            connectChannel(ip, port, 0);
        }
    }

    public void connectChannel(String ip, int port, int reConnectCount) {
        log.info("Led Controller Connect Start ip: {} port : {}", ip, port);

        Bootstrap bootstrap = new Bootstrap();

        bootstrap
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(tcpClientHandler);
                    }
                }).connect(ip, port).addListener((ChannelFuture channelFuture) -> {
                    if (channelFuture.isSuccess()) {
                        Channel channel = channelFuture.channel();
                        channelFutureMap.put(channel, new CompletableFuture<>());
                    } else {
                        log.error("Failed to Led Controller Connect. Retrying in 5 seconds... Because {}", String.valueOf(channelFuture.cause()));

                        if (reConnectCount < SOCKET_RE_CONNECT_COUNT) {
                            channelFuture.channel().eventLoop().schedule(() -> connectChannel(ip, port, reConnectCount + 1), 5, TimeUnit.SECONDS);
                        }
                    }
                });
    }

    public CompletableFuture<ResponseWithIpVo[]> sendMessage(byte[] message) {
        List<CompletableFuture<ResponseWithIpVo>> futures = new ArrayList<>();

        // send it to the server
        for (Map.Entry<Channel, CompletableFuture<ResponseWithIpVo>> entry : channelFutureMap.entrySet()) {
            Channel channel = entry.getKey();
            CompletableFuture<ResponseWithIpVo> future = new CompletableFuture<>();
            futures.add(future);

            if (channel != null && channel.isActive()) {
                // detect sender 메세지 체크 true 이면 detect sender 이므로 길이 체크 수행
                detectSenderMessage.set(checkDetectSenderMessage(message));

                ByteBuf buffer = Unpooled.wrappedBuffer(message);

                ChannelFuture channelFuture = channel.writeAndFlush(buffer);

                channelFuture.addListener((ChannelFutureListener) future1 -> {
                    if (!future1.isSuccess()) {
                        Throwable cause = future1.cause();
                        log.error("Send Packet To LED Controller Fail", cause);
                        future.completeExceptionally(cause);
                    } else {
                        log.info("Send Packet {} To LED Controller Success", message);
                    }
                });
            } else {
                log.error("No Active LED Controller Connection");
                future.completeExceptionally(new RuntimeException("No active LED Controller Connection"));
            }

            channelFutureMap.put(channel, future);
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> {
                    // Get the responses from channelFutureMap along with IP address
                    ResponseWithIpVo[] responses = new ResponseWithIpVo[futures.size()];
                    for (int i = 0; i < futures.size(); i++) {
                        CompletableFuture<ResponseWithIpVo> future = futures.get(i);
                        try {
                            byte[] response = future.get().getResponse();
                            String ip = future.get().getIp();
                            responses[i] = new ResponseWithIpVo(response, ip);
                        } catch (InterruptedException | ExecutionException e) {
                            log.error("Failed to get led controller response with IP address", e);
                        }
                    }
                    return responses;
                });
    }

    @PreDestroy
    public void stop() throws InterruptedException {
        for (Map.Entry<Channel, CompletableFuture<ResponseWithIpVo>> entry : channelFutureMap.entrySet()) {
            Channel channel = entry.getKey();

            if (channel != null) {
                channel.close().sync();
                CompletableFuture<ResponseWithIpVo> future = channelFutureMap.remove(channel); // 맵에서 CompletableFuture 제거함

                if (future != null) {
                    future.completeExceptionally(new RuntimeException("Led Controller Channel closed"));
                }
            }
        }

        if (group != null) {
            group.shutdownGracefully().sync();
        }
    }

    private boolean checkDetectSenderMessage(byte[] message) {
        byte[] ledStatusMessage = {0x01, 0x00, 0x11, 0x00, 0x00, 0x00, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x16};

        return Arrays.equals(message, ledStatusMessage);
    }

    @ChannelHandler.Sharable
    private class TcpClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

        private final int HEARTBEAT_INTERVAL_SECONDS = 1; // 하트비트 간격
        private ScheduledFuture<?> heartbeatTask;
        private ChannelHandlerContext ctx;

        /**
         * * LED 컨트롤러에 하트비트 전송
         */
        private void sendHeartbeatMessage() {
            ByteBuf heartbeatMessage = ctx.alloc().buffer();
            byte[] heartbeat = {(byte) 0x99, (byte) 0x99, 0x04, 0x00};

            heartbeatMessage.writeBytes(heartbeat);

            // Heartbeat 메시지 전송
            ctx.writeAndFlush(Unpooled.wrappedBuffer(heartbeatMessage));

            log.info("Heartbeat send to Led Controller");
        }

        /**
         * * IP 가져오기
         */
        private String getIpAddressFromChannel(Channel channel) {
            String ip = "";
            SocketAddress remoteAddress = channel.remoteAddress();

            if (remoteAddress instanceof InetSocketAddress) {
                InetSocketAddress inetAddress = (InetSocketAddress) remoteAddress;
                ip = inetAddress.getHostString();
            }
            return ip;
        }

        /**
         * * TCP 통신 후 Response 값 처리
         */
        @Override
        public void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
            byte[] responseByte = new byte[msg.readableBytes()];
            msg.readBytes(responseByte);

            CompletableFuture<ResponseWithIpVo> future = channelFutureMap.get(ctx.channel());

            if (future != null && !bytesToHexString(responseByte).equals("99990400")) {
                String ip = getIpAddressFromChannel(ctx.channel()); // IP 주소 가져오기

                if (!responseFragmentMap.containsKey(ctx.channel())) {
                    // 채널에 대한 응답 값 조각이 없는 경우, 새로운 응답 값 조각 맵을 생성
                    responseFragmentMap.put(ctx.channel(), new ArrayList<>());
                }

                List<byte[]> fragmentList = responseFragmentMap.get(ctx.channel());
                fragmentList.add(responseByte);

                // 응답 값 길이 체크, detect Sender 메세지가 아닐 경우 길이체크 x
                if (isResponseComplete(fragmentList) || !detectSenderMessage.get()) {
                    byte[] assembledData = assembleResponse(fragmentList); // 하나의 응답 값으로 생성
                    ResponseWithIpVo responseWithIp = new ResponseWithIpVo(assembledData, ip);

                    // CompletableFuture 완료
                    future.complete(responseWithIp);

                    // 응답 값 조각 맵에서 제거
                    responseFragmentMap.remove(ctx.channel());
                }
            }
        }

        /**
         * * 하트비트 패킷 구별
         */
        private String bytesToHexString(byte[] bytes) {
            StringBuilder hexString = new StringBuilder();
            for (byte b : bytes) {
                hexString.append(String.format("%02X", b));
            }
            return hexString.toString();
        }

        /**
         * * 응답 값 길이 체크
         */
        private boolean isResponseComplete(List<byte[]> fragmentList) {
            int totalLength = fragmentList.stream()
                    .mapToInt(response -> response.length)
                    .sum();

            return totalLength >= EXPECTED_TOTAL_LENGTH;
        }

        /**
         * * 응답 값을 합치기
         */
        private byte[] assembleResponse(List<byte[]> fragmentList) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            for (byte[] fragment : fragmentList) {
                try {
                    outputStream.write(fragment);
                } catch (IOException e) {
                    log.error("Error while assembling response", e);
                }
            }
            return outputStream.toByteArray();
        }

        /**
         * * TCP server 연결 끊겼을 경우
         */
        @Override
        public void channelInactive(ChannelHandlerContext ctx) {
            String ip = "";
            int port = 0;

            SocketAddress remoteAddress = ctx.channel().remoteAddress();

            if (remoteAddress instanceof InetSocketAddress) {
                InetSocketAddress inetAddress = (InetSocketAddress) remoteAddress;
                ip = inetAddress.getHostString();
                port = inetAddress.getPort();
            }

            log.error("Lost connection to Led Controller. Reconnecting to {}:{}", ip, port);

            // 하트비트 전송 작업 취소
            heartbeatTask.cancel(true);

            Channel channel = ctx.channel();

            CompletableFuture<ResponseWithIpVo> future = channelFutureMap.remove(channel); // 맵에서 CompletableFuture 제거함
            if (future != null) {
                future.completeExceptionally(new RuntimeException("Led Channel Inactive"));
            }

            connectChannel(ip, port, 0);
        }

        /**
         * * TCP server 연결 성공 했을 경우
         */
        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            log.info("Connected to Led Controller");
            this.ctx = ctx;

            // 최초 연결 시 스케줄링된 하트비트 전송 작업 생성
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            heartbeatTask = executor.scheduleAtFixedRate(
                    this::sendHeartbeatMessage, // 하트비트 전송 작업
                    0,
                    HEARTBEAT_INTERVAL_SECONDS, // 주기적인 간격
                    TimeUnit.SECONDS
            );

            Channel channel = ctx.channel();

            channelFutureMap.computeIfPresent(channel, (c, future) -> {
                future.completeExceptionally(new RuntimeException("Led Channel reconnected"));
                return new CompletableFuture<>(); // 재연결된 채널에 대해 새로운 CompletableFuture 생성함
            });
        }
    }
}
