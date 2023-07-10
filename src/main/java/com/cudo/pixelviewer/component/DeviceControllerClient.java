package com.cudo.pixelviewer.component;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class DeviceControllerClient {
    private EventLoopGroup group;
    private Channel channel;
    private DeviceClientHandler deviceClientHandler;
    private Object responseLock; // 동기화를 위한 객체
    private ByteBuf response;

    @PostConstruct
    public void start() throws Exception {
        group = new NioEventLoopGroup();
        deviceClientHandler = new DeviceClientHandler();
        responseLock = new Object();

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


        bootstrap.connect("192.168.0.15", 6001).addListener((ChannelFuture future) -> {
            if (future.isSuccess()) {
                channel = future.channel();
            } else {
                log.error("Failed to Device Controller Connect. Retrying in 5 seconds... Because {}", String.valueOf(future.cause()));

                future.channel().eventLoop().schedule(() -> connect(bootstrap, eventLoop), 5, TimeUnit.SECONDS);
            }
        });
    }

    public void sendMessage(byte[] message) {
        // send it to the server
        if (channel != null && channel.isActive()) {
            ByteBuf buffer = Unpooled.wrappedBuffer(message);

            ChannelFuture future = channel.writeAndFlush(buffer);

            future.addListener((ChannelFutureListener) future1 -> {
                if (!future1.isSuccess()) {
                    Throwable cause = future1.cause();
                    log.error("SEND PACKET TO DEVICE CONTROLLER FAIL", cause);
                } else {
                    log.info("SEND PACKET {} TO DEVICE CONTROLLER SUCCESS", message);
                }
            });
        } else {
            log.error("NO ACTIVE DEVICE CONTROLLER CONNECTION");
        }
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

    public String getResponse() {
        synchronized (responseLock) {
            try {
                while (response == null) {
                    responseLock.wait(); // 응답이 도착할 때까지 대기
                }

                // 응답 처리 로직을 여기에 작성
                byte[] responseByte = new byte[response.readableBytes()];
                response.readBytes(responseByte);

                StringBuilder hexString = new StringBuilder();
                for (byte responseValue : responseByte) {
                    String hex = String.format("%02X", responseValue);
                    hexString.append(hex);
                }

                // 응답 처리가 완료되었으므로 response 변수 초기화
                response = null;

                return hexString.toString();

            } catch (InterruptedException e) {
                log.error("Device Controller Response Interrupted", e);
                Thread.currentThread().interrupt();

                return "";
            }
        }
    }

    @ChannelHandler.Sharable
    private class DeviceClientHandler extends ChannelInboundHandlerAdapter {
        private ChannelHandlerContext ctx;

        /**
         * * TCP 통신 후 Response 값 처리
         */
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            ByteBuf response = (ByteBuf) msg; // 수신한 응답을 임시로 저장할 변수

            synchronized (responseLock) {
                if (response != null) {
                    if (DeviceControllerClient.this.response != null) {
                        DeviceControllerClient.this.response.release();
                    }
                    DeviceControllerClient.this.response = response.retain(); // 새로운 응답을 response 변수에 할당하고 참조 카운트를 유지
                    responseLock.notify(); // 대기 중인 getResponse() 메소드에 신호를 보냄
                }
            }

            ctx.writeAndFlush(msg);
            ReferenceCountUtil.release(msg);
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
