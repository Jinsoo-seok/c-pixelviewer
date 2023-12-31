package com.cudo.pixelviewer.component;

import com.cudo.pixelviewer.bo.mapper.PwrconMapper;
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
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class DeviceControllerClient {
    private EventLoopGroup group;
    private DeviceClientHandler deviceClientHandler;
    private Map<Channel, CompletableFuture<ResponseWithIpVo>> channelFutureMap;
    private Set<Channel> channelCloseSet;

    final PwrconMapper pwrconMapper;

    private final static Integer SOCKET_RE_CONNECT_COUNT = 3;

    public DeviceControllerClient(final PwrconMapper pwrconMapper) {
        this.pwrconMapper = pwrconMapper;
    }

    @PostConstruct
    public void start() throws Exception {
        group = new NioEventLoopGroup();
        deviceClientHandler = new DeviceClientHandler();
        channelFutureMap = new ConcurrentHashMap<>();
        channelCloseSet = new HashSet<>();

        connect(); // 초기 연결
    }

    public Map<Channel, CompletableFuture<ResponseWithIpVo>> getChannelFutureMap() {
        return this.channelFutureMap;
    }

    public void setChannelFutureMap(Map<Channel, CompletableFuture<ResponseWithIpVo>> channelFutureMap) {
        this.channelFutureMap = channelFutureMap;

    }

    private void connect() {
        List<PwrconVo> ipPortList = pwrconMapper.getPwrconList();

        for (PwrconVo ipPort : ipPortList) {
            String ip = ipPort.getIp();
            Integer port = ipPort.getPort();

            connectChannel(ip, port, 0);
        }
    }

    public void connectChannel(String ip, int port, int reConnectCount) {
        log.info("Device Unit Controller Connect Start ip: {} port : {}", ip, port);

        Bootstrap bootstrap = new Bootstrap();

        bootstrap
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(deviceClientHandler);
                    }
                }).connect(ip, port).addListener((ChannelFuture future) -> {
                    if (future.isSuccess()) {
                        Channel channel = future.channel();
                        channelFutureMap.put(channel, new CompletableFuture<>());

                        if (channelCloseSet.contains(channel)) {
                            channelCloseSet.remove(channel);
                        }
                    } else {
                        log.error("Failed to Device Unit Controller Connect. Retrying in 5 seconds... Because {}", String.valueOf(future.cause()));

                        if (reConnectCount < SOCKET_RE_CONNECT_COUNT) {
                            future.channel().eventLoop().schedule(() -> connectChannel(ip, port, reConnectCount + 1), 5, TimeUnit.SECONDS);
                        }
                    }
                });
    }

    public void channelClose(Channel channel) {
        channel.close();
        channelCloseSet.add(channel);
    }

    public CompletableFuture<ResponseWithIpVo[]> sendMessage(byte[] message) {
        List<CompletableFuture<ResponseWithIpVo>> futures = new ArrayList<>();

        log.info("Start Sending Packet to Unit Controller >> {}", channelFutureMap);
        // send it to the server
        for (Map.Entry<Channel, CompletableFuture<ResponseWithIpVo>> entry : channelFutureMap.entrySet()) {
            Channel channel = entry.getKey();
            CompletableFuture<ResponseWithIpVo> future = new CompletableFuture<>();
            futures.add(future);

            if (channel != null && channel.isActive()) {
                log.error("Active Device Unit Controller");

                ByteBuf buffer = Unpooled.wrappedBuffer(message);

                ChannelFuture channelFuture = channel.writeAndFlush(buffer);

                channelFuture.addListener((ChannelFutureListener) future1 -> {
                    if (!future1.isSuccess()) {
                        Throwable cause = future1.cause();
                        log.error("Send Packet To Device Unit Controller Fail", cause);
                        future.completeExceptionally(cause);
                    } else {
                        log.info("Send Packet {} To Device Unit Controller Success", message);
                    }
                });
            } else {
                log.error("No Active Device Unit Controller Connection");
                future.completeExceptionally(new RuntimeException("No active Device Unit Controller Connection"));
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
                            log.error("Failed to get device unit controller response with IP address", e);
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
                    future.completeExceptionally(new RuntimeException("Device Unit Controller Channel closed"));
                }
            }
        }

        if (group != null) {
            group.shutdownGracefully().sync();
        }
    }

    @ChannelHandler.Sharable
    private class DeviceClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
        private ChannelHandlerContext ctx;

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

            if (future != null) {
                String ip = getIpAddressFromChannel(ctx.channel()); // IP 주소 가져오기
                ResponseWithIpVo responseWithIp = new ResponseWithIpVo(responseByte, ip);

                future.complete(responseWithIp);
            }
        }

        /**
         * * TCP server 연결 끊겼을 경우
         */
        @Override
        public void channelInactive(ChannelHandlerContext ctx) {
            log.error("Lost connection to Device Unit Controller. Reconnecting...");

            String ip = "";
            int port = 0;

            SocketAddress remoteAddress = ctx.channel().remoteAddress();

            if (remoteAddress instanceof InetSocketAddress) {
                InetSocketAddress inetAddress = (InetSocketAddress) remoteAddress;
                ip = inetAddress.getHostString();
                port = inetAddress.getPort();
            }

            Channel channel = ctx.channel();

            CompletableFuture<ResponseWithIpVo> future = channelFutureMap.remove(channel); // 맵에서 CompletableFuture 제거함
            if (future != null) {
                future.completeExceptionally(new RuntimeException("Device Unit Channel Inactive"));
            }

            if (!channelCloseSet.contains(channel)) {
                connectChannel(ip, port, 0);
            }
        }

        /**
         * * TCP server 연결 성공 했을 경우
         */
        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            log.info("Connected to Device Controller");
            this.ctx = ctx;

            Channel channel = ctx.channel();

            channelFutureMap.computeIfPresent(channel, (c, future) -> {
                future.completeExceptionally(new RuntimeException("Device Unit Channel reconnected"));
                return new CompletableFuture<>(); // 재연결된 채널에 대해 새로운 CompletableFuture 생성함
            });
        }
    }
}
