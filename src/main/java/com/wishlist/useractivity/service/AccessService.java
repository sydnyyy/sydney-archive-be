package com.wishlist.useractivity.service;

import com.wishlist.global.websocket.manager.WebSocketSessionManager;
import com.wishlist.useractivity.entity.AccessEvent;
import com.wishlist.useractivity.manager.UserAccessManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccessService {

    private final WebSocketSessionManager webSocketSessionManager;
    private final UserAccessManager userAccessManager;

    public void recordAccess(AccessEvent accessEvent) {
        webSocketSessionManager.updateTTL(accessEvent.clientId());
        userAccessManager.recordAccess(accessEvent.clientId());
    }
}
