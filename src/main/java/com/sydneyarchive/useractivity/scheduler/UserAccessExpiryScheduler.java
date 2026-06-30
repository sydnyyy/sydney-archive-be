package com.sydneyarchive.useractivity.scheduler;

import com.sydneyarchive.global.websocket.manager.WebSocketSessionManager;
import com.sydneyarchive.useractivity.manager.UserAccessManager;
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
    public void expireInactiveSids() {
        long cutoff = System.currentTimeMillis() - EXPIRY_MILLIS;
        List<String> expiredSids = userAccessManager.removeExpiredSids(cutoff);

        if (!expiredSids.isEmpty()) {
            log.info("[expireInactiveSids] Expired sid detected: {}", expiredSids);
        }

        for (String sid : expiredSids) {
            if (!userAccessManager.hasActiveSid(sid)) {
                webSocketSessionManager.removeAllSessions(sid);
            }
        }
    }
}
