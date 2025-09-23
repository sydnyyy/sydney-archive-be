package com.wishlist.useractivity.manager;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

@Component
public class UserAccessManager {

    private final Map<String, Long> lastAccessTimeMap = new ConcurrentHashMap<>();
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
        Long previousTimestamp = lastAccessTimeMap.get(clientId);
        if (previousTimestamp != null) {
            accessSortedSet.remove(new AccessEntry(previousTimestamp, clientId));
        }

        long now = System.currentTimeMillis();
        lastAccessTimeMap.put(clientId, now);
        accessSortedSet.add(new AccessEntry(now, clientId));
    }

    public List<String> removeExpiredClientIds(long cutoff) {
        List<String> expiredClientIds = new ArrayList<>();
        Iterator<AccessEntry> iterator = accessSortedSet.iterator();
        while (iterator.hasNext()) {
            AccessEntry entry = iterator.next();
            if (entry.timestamp > cutoff) {
                break;
            }

            Long lastAccessTime = lastAccessTimeMap.get(entry.clientId);
            if (lastAccessTime != null && Objects.equals(lastAccessTime, entry.timestamp)) {
                iterator.remove();
                lastAccessTimeMap.remove(entry.clientId);
                expiredClientIds.add(entry.clientId);
            }
        }
        return expiredClientIds;
    }

    public boolean hasActiveClient(String clientId) {
        return lastAccessTimeMap.containsKey(clientId);
    }
}
