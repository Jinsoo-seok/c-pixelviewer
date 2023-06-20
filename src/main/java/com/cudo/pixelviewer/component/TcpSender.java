package com.cudo.pixelviewer.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.ip.tcp.TcpSendingMessageHandler;
import org.springframework.integration.ip.tcp.connection.CachingClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpConnectionSupport;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

@Component
public class TcpSender {

    private final TcpSendingMessageHandler tcpSendingMessageHandler;
    private final CachingClientConnectionFactory cachingClientConnectionFactory;

    @Autowired
    public TcpSender(TcpSendingMessageHandler tcpSendingMessageHandler, CachingClientConnectionFactory cachingClientConnectionFactory) {
        this.tcpSendingMessageHandler = tcpSendingMessageHandler;
        this.cachingClientConnectionFactory = cachingClientConnectionFactory;
    }

    public void sendMessage(byte[] payload) {

        boolean isRunning = cachingClientConnectionFactory.isRunning();
        if (isRunning) {
            Message<byte[]> message = MessageBuilder.withPayload(payload)
                    .setHeader(MessageHeaders.CONTENT_TYPE, "application/octet-stream")
                    .build();

            TcpConnectionSupport connection = null;
            try {
                connection = cachingClientConnectionFactory.getConnection();
                tcpSendingMessageHandler.handleMessage(message);
                // 메시지를 보낸 후에 소켓을 닫습니다.
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                // 예외 처리
                e.printStackTrace();
                if (connection != null) {
                    connection.close();
                }
            }
        }
        else {
        }
    }
}
