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

    private static final long EXPIRY_MILLIS = 40 * 60_000L;

    private final UserAccessManager userAccessManager;
    private final WebSocketSessionManager webSocketSessionManager;

    @Scheduled(cron = "0 * * * * *")
    public void expireInactiveUids() {
        long cutoff = System.currentTimeMillis() - EXPIRY_MILLIS;
        List<String> expiredUids = userAccessManager.removeExpiredUids(cutoff);

        if (!expiredUids.isEmpty()) {
            log.info("[expireInactiveUids] Expired uid detected: {}", expiredUids);
        }

        for (String uid : expiredUids) {
            if (!userAccessManager.hasActiveUid(uid)) {
                webSocketSessionManager.removeAllSessions(uid);
            }
        }
    }
}
