package com.wishlist.useractivity.service;

import com.wishlist.global.websocket.manager.WebSocketSessionManager;
import com.wishlist.useractivity.entity.AccessEvent;
import com.wishlist.useractivity.manager.UserAccessManager;
import com.wishlist.useractivity.repository.AccessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccessService {

    private final AccessRepository accessRepository;
    private final WebSocketSessionManager webSocketSessionManager;
    private final UserAccessManager userAccessManager;

    public void recordAccess(AccessEvent accessEvent) {
        accessRepository.save(accessEvent);
        webSocketSessionManager.updateTTL(accessEvent.clientId());
        userAccessManager.recordAccess(accessEvent.clientId());
    }
}
