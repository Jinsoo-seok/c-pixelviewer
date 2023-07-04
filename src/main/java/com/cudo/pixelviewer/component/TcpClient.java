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

@Component
@Slf4j
public class TcpClient {

    private EventLoopGroup group;
    private Channel channel;

    @PostConstruct
    public void start() throws Exception {
        group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ChannelPipeline pipeline = ch.pipeline();

                        pipeline.addLast(new TcpClientHandler());
                    }
                });

        ChannelFuture future = bootstrap.connect("192.168.0.15", 6001).sync();
        channel = future.channel();

        log.info("Connected to TCP server");
    }

    public void sendMessage(byte[] message) {
        // send it to the server
        if (channel != null && channel.isActive()) {
            ByteBuf buffer = Unpooled.wrappedBuffer(message);

            ChannelFuture future = channel.writeAndFlush(buffer);

            future.addListener((ChannelFutureListener) future1 -> {
                if (!future1.isSuccess()) {
                    Throwable cause = future1.cause();
                    log.error("SEND PACKET TO TCP SERVER FAIL", cause);
                }
            });
        } else {
            log.error("NO ACTIVE TCP CONNECTION");
        }
    }

    @PreDestroy
    public void stop() throws InterruptedException {
        System.out.println("disconnect TCP server");
        if (channel != null) {
            channel.close().sync();
        }
        if (group != null) {
            group.shutdownGracefully().sync();
        }
    }

    /**
     * * TCP 통신 후 Response 값 처리
     */
    private static class TcpClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {

            byte[] responseByte = new byte[msg.readableBytes()];
            msg.readBytes(responseByte);

            // TODO Response 값 활용

            // Send a response back to the client
            ctx.writeAndFlush(responseByte);
        }
    }
}
