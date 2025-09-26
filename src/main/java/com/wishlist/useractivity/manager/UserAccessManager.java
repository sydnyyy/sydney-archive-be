package com.wishlist.useractivity.manager;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
@Slf4j
public class UserAccessManager {

    private static final int STRIPE_COUNT = 64;
    private static final Lock[] locks = new Lock[STRIPE_COUNT];

    static {
        for (int i = 0; i < locks.length; i++) {
            locks[i] = new ReentrantLock();
        }
    }

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

    private static class LockWithIndex {
        final Lock lock;
        final int index;

        LockWithIndex(Lock lock, int index) {
            this.lock = lock;
            this.index = index;
        }

        void lock() { lock.lock(); }
        void unlock() { lock.unlock(); }
    }

    private LockWithIndex getLock(String clientId) {
        int index = clientId.hashCode() & (STRIPE_COUNT - 1);
        return new LockWithIndex(locks[index], index);
    }

    public void recordAccess(String clientId) {
        LockWithIndex lock = getLock(clientId);
        lock.lock();
        try {
            log.info("[recordAccess] clientId='{}' acquired lock index={}", clientId, lock.index);

            long now = System.currentTimeMillis();
            Long previousTimestamp = lastAccessTimeMap.get(clientId);
            if (previousTimestamp != null && previousTimestamp > now) {
                return;
            }

            if (previousTimestamp != null) {
                accessSortedSet.remove(new AccessEntry(previousTimestamp, clientId));
            }

            lastAccessTimeMap.put(clientId, now);
            accessSortedSet.add(new AccessEntry(now, clientId));
        } finally {
            lock.unlock();
            log.info("[recordAccess] clientId='{}' released lock index={}", clientId, lock.index);
        }
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
