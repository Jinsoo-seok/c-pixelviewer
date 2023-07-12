package com.cudo.pixelviewer.component;

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
import java.util.concurrent.*;

@Component
@Slf4j
public class LedControllerClient {

    private EventLoopGroup group;
    private Channel channel;
    private TcpClientHandler tcpClientHandler;
    private CompletableFuture<byte[]> responseFuture;


    @PostConstruct
    public void start() throws Exception {
        group = new NioEventLoopGroup();
        tcpClientHandler = new TcpClientHandler();

        connect(new Bootstrap(), group);
    }

    private void connect(Bootstrap bootstrap, EventLoopGroup eventLoop) {
        log.info("Led Controller Connect Start {} : {}", bootstrap, eventLoop);
        bootstrap.group(eventLoop)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(tcpClientHandler);
                    }
                });

        // TODO 재연결 예외처리 추가 필요
        bootstrap.connect("192.168.0.201", 9999).addListener((ChannelFuture future) -> {
            if (future.isSuccess()) {
                channel = future.channel();
            } else {
                log.error("Failed to Led Controller Connect. Retrying in 5 seconds... Because {}", String.valueOf(future.cause()));

                future.channel().eventLoop().schedule(() -> connect(bootstrap, eventLoop), 5, TimeUnit.SECONDS);
            }
        });
    }

    public CompletableFuture<byte[]> sendMessage(byte[] message) {
        CompletableFuture<byte[]> future = new CompletableFuture<>();
        responseFuture = future;

        // send it to the server
        if (channel != null && channel.isActive()) {
            ByteBuf buffer = Unpooled.wrappedBuffer(message);

            ChannelFuture channelFuture = channel.writeAndFlush(buffer);

            channelFuture.addListener((ChannelFutureListener) future1 -> {
                if (!future1.isSuccess()) {
                    Throwable cause = future1.cause();
                    log.error("SEND PACKET TO LED CONTROLLER FAIL", cause);
                    future.completeExceptionally(cause);
                } else {
                    log.info("SEND PACKET {} TO LED CONTROLLER SUCCESS", message);
                }
            });
        } else {
            log.error("NO ACTIVE LED CONTROLLER CONNECTION");
            future.completeExceptionally(new RuntimeException("No active LED controller connection"));
        }

        return future;
    }

    @PreDestroy
    public void stop() throws InterruptedException {
        if (channel != null) {
            channel.close().sync();
        }
        if (group != null) {
            group.shutdownGracefully().sync();
        }
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
         * * TCP 통신 후 Response 값 처리
         */
        @Override
        public void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
            byte[] responseByte = new byte[msg.readableBytes()];
            msg.readBytes(responseByte);

            StringBuilder hexString = new StringBuilder();
            for (byte responseValue : responseByte) {
                String hex = String.format("%02X", responseValue);
                hexString.append(hex);
            }

            if (responseFuture != null && !hexString.toString().equals("99990400")) {
                responseFuture.complete(responseByte);
            }
        }

        /**
         * * TCP server 연결 끊겼을 경우
         */
        @Override
        public void channelInactive(ChannelHandlerContext ctx) {
            log.error("Lost connection to Led Controller. Reconnecting...");

            // 하트비트 전송 작업 취소
            heartbeatTask.cancel(true);

            connect(new Bootstrap(), ctx.channel().eventLoop().parent());
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
        }
    }
}
