package com.cudo.pixelviewer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.ip.tcp.TcpSendingMessageHandler;
import org.springframework.integration.ip.tcp.connection.AbstractClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.CachingClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpNetClientConnectionFactory;

@Configuration
@EnableIntegration
public class TcpConfig {

    @Bean
    public TcpSendingMessageHandler tcpSendingMessageHandler(CachingClientConnectionFactory clientConnectionFactory) {
        TcpSendingMessageHandler messageHandler = new TcpSendingMessageHandler();
        messageHandler.setConnectionFactory(clientConnectionFactory);
        return messageHandler;
    }

    @Bean
    public TcpNetClientConnectionFactory clientConnectionFactory() {
        TcpNetClientConnectionFactory clientConnectionFactory = new TcpNetClientConnectionFactory("192.168.123.89", 6001);
        return clientConnectionFactory;
    }

    @Bean
    public CachingClientConnectionFactory cachingClientConnectionFactory(AbstractClientConnectionFactory clientConnectionFactory) {
        CachingClientConnectionFactory cachingClientConnectionFactory = new CachingClientConnectionFactory(clientConnectionFactory, 20);
        cachingClientConnectionFactory.setSoTimeout(1000);
        cachingClientConnectionFactory.start();

        return cachingClientConnectionFactory;
    }
}