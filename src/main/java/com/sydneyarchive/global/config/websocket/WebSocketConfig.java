package com.sydneyarchive.global.config.websocket;

import com.sydneyarchive.global.config.web.CorsProperties;
import com.sydneyarchive.global.websocket.handler.WebSocketSessionHandler;
import com.sydneyarchive.global.websocket.handler.WebSocketHandShakeHandler;
import com.sydneyarchive.global.websocket.interceptor.StompInterceptor;
import com.sydneyarchive.global.websocket.interceptor.WebSocketHandshakeInterceptor;
import com.sydneyarchive.global.websocket.manager.WebSocketSessionManager;
import com.sydneyarchive.useractivity.manager.UserAccessManager;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@EnableConfigurationProperties(CorsProperties.class)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final String ENDPOINT = "/ws";

    private final WebSocketHandshakeInterceptor webSocketHandshakeInterceptor;
    private final StompInterceptor stompInterceptor;
    private final WebSocketHandShakeHandler webSocketHandShakeHandler;

    private final WebSocketSessionManager webSocketSessionManager;
    private final UserAccessManager userAccessManager;

    private final CorsProperties corsProperties;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(ENDPOINT)
                .addInterceptors(webSocketHandshakeInterceptor)
                .setHandshakeHandler(webSocketHandShakeHandler)
                .setAllowedOriginPatterns(corsProperties.allowedOrigins());
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/queue", "/topic");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompInterceptor);
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.addDecoratorFactory(handler
                -> new WebSocketSessionHandler(handler, webSocketSessionManager, userAccessManager));
    }
}
