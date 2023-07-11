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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class DeviceControllerClient {
    private EventLoopGroup group;
    private Channel channel;
    private DeviceClientHandler deviceClientHandler;
    private CompletableFuture<String> responseFuture;

    @PostConstruct
    public void start() throws Exception {
        group = new NioEventLoopGroup();
        deviceClientHandler = new DeviceClientHandler();

        connect(new Bootstrap(), group);
    }

    private void connect(Bootstrap bootstrap, EventLoopGroup eventLoop) {
        log.info("Device Controller Connect Start");
        bootstrap.group(eventLoop)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(deviceClientHandler);
                    }
                });

        // TODO 재연결 예외처리 추가 필요
        bootstrap.connect("192.168.0.15", 6001).addListener((ChannelFuture future) -> {
            if (future.isSuccess()) {
                channel = future.channel();
            } else {
                log.error("Failed to Device Controller Connect. Retrying in 5 seconds... Because {}", String.valueOf(future.cause()));

                future.channel().eventLoop().schedule(() -> connect(bootstrap, eventLoop), 5, TimeUnit.SECONDS);
            }
        });
    }

    public CompletableFuture<String> sendMessage(byte[] message) {
        CompletableFuture<String> future = new CompletableFuture<>();
        responseFuture = future;

        // send it to the server
        if (channel != null && channel.isActive()) {
            ByteBuf buffer = Unpooled.wrappedBuffer(message);

            ChannelFuture channelFuture = channel.writeAndFlush(buffer);

            channelFuture.addListener((ChannelFutureListener) future1 -> {
                if (!future1.isSuccess()) {
                    Throwable cause = future1.cause();
                    log.error("SEND PACKET TO DEVICE CONTROLLER FAIL", cause);
                    future.completeExceptionally(cause);
                } else {
                    log.info("SEND PACKET {} TO DEVICE CONTROLLER SUCCESS", message);
                }
            });
        } else {
            log.error("NO ACTIVE DEVICE CONTROLLER CONNECTION");
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
    private class DeviceClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
        private ChannelHandlerContext ctx;

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

            if (responseFuture != null) {
                responseFuture.complete(hexString.toString());
            }
        }

        /**
         * * TCP server 연결 끊겼을 경우
         */
        @Override
        public void channelInactive(ChannelHandlerContext ctx) {
            log.error("Lost connection to Device Controller. Reconnecting...");

            connect(new Bootstrap(), ctx.channel().eventLoop().parent());
        }

        /**
         * * TCP server 연결 성공 했을 경우
         */
        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            log.info("Connected to Device Controller");
            this.ctx = ctx;
        }
    }
}
