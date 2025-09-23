package com.wishlist.useractivity.manager;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

@Component
public class UserAccessManager {

    private final Map<String, Long> lastAccessTime = new ConcurrentHashMap<>();
    private final Set<AccessEntry> accessSortedSet = new ConcurrentSkipListSet<>();

    static class AccessEntry implements Comparable<AccessEntry> {

        final long timestamp;
        final String clientId;

        AccessEntry(long timestamp, String clientId) {
            this.timestamp = timestamp;
            this.clientId = clientId;
        }

        @Override
        public int compareTo(@NotNull AccessEntry o) {
            int cmp = Long.compare(this.timestamp, o.timestamp);
            if (cmp != 0) return cmp;
            return this.clientId.compareTo(o.clientId);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof AccessEntry)) return false;
            AccessEntry that = (AccessEntry) o;
            return timestamp == that.timestamp && clientId.equals(that.clientId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(timestamp, clientId);
        }
    }

    public void recordAccess(String clientId) {
        Long previousTimestamp = lastAccessTime.get(clientId);
        if (previousTimestamp != null) {
            accessSortedSet.remove(new AccessEntry(previousTimestamp, clientId));
        }

        long now = System.currentTimeMillis();
        lastAccessTime.put(clientId, now);
        accessSortedSet.add(new AccessEntry(now, clientId));
    }
}
