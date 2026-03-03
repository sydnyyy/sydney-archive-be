package com.theforbiddenland.global.websocket.interceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompInterceptor implements ChannelInterceptor {

    private static final Set<StompCommand> COMMANDS_TO_AUTH = Set.of(
            StompCommand.CONNECT,
            StompCommand.SUBSCRIBE,
            StompCommand.SEND
    );

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(message);

        log.info("[preSend] " + stompHeaderAccessor);

        return MessageBuilder.createMessage(message.getPayload(), stompHeaderAccessor.getMessageHeaders());
    }
}
