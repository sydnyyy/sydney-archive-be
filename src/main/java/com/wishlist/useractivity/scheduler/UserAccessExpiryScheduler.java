package com.wishlist.useractivity.scheduler;

import com.wishlist.global.websocket.manager.WebSocketSessionManager;
import com.wishlist.useractivity.manager.UserAccessManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserAccessExpiryScheduler {

    private static final long EXPIRY_MILLIS = 30 * 60_000L;

    private final UserAccessManager userAccessManager;
    private final WebSocketSessionManager webSocketSessionManager;

    @Scheduled(cron = "0 * * * * *")
    public void expireInactiveClients() {
        long cutoff = System.currentTimeMillis() - EXPIRY_MILLIS;
        List<String> expiredClientIds = userAccessManager.removeExpiredClientIds(cutoff);

        if (!expiredClientIds.isEmpty()) {
            log.info("[expireInactiveClients] Expired clients detected: {}", expiredClientIds);
        }

        for (String clientId : expiredClientIds) {
            if (!userAccessManager.hasActiveClient(clientId)) {
                webSocketSessionManager.removeAllSessions(clientId);
            }
        }
    }
}
