package com.theforbiddenland.useractivity.manager;

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

    record AccessEntry(long timestamp, String uid) implements Comparable<AccessEntry> {

        @Override
            public int compareTo(@NotNull AccessEntry o) {
                int cmp = Long.compare(this.timestamp, o.timestamp);
                if (cmp != 0) return cmp;
                return this.uid.compareTo(o.uid);
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof AccessEntry(long timestamp, String uid))) return false;
                return this.timestamp == timestamp && this.uid.equals(uid);
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

    private LockWithIndex getLock(String uid) {
        int index = uid.hashCode() & (STRIPE_COUNT - 1);
        return new LockWithIndex(locks[index], index);
    }

    public void recordAccess(String uid) {
        LockWithIndex lock = getLock(uid);
        lock.lock();
        try {
            log.info("[recordAccess] uid='{}' acquired lock index={}", uid, lock.index);

            long now = System.currentTimeMillis();
            Long previousTimestamp = lastAccessTimeMap.get(uid);
            if (previousTimestamp != null && previousTimestamp > now) {
                return;
            }

            if (previousTimestamp != null) {
                accessSortedSet.remove(new AccessEntry(previousTimestamp, uid));
            }

            lastAccessTimeMap.put(uid, now);
            accessSortedSet.add(new AccessEntry(now, uid));
        } finally {
            lock.unlock();
            log.info("[recordAccess] uid='{}' released lock index={}", uid, lock.index);
        }
    }

    public List<String> removeExpiredUids(long cutoff) {
        List<String> expiredUids = new ArrayList<>();
        Iterator<AccessEntry> iterator = accessSortedSet.iterator();
        while (iterator.hasNext()) {
            AccessEntry entry = iterator.next();
            if (entry.timestamp > cutoff) {
                break;
            }

            Long lastAccessTime = lastAccessTimeMap.get(entry.uid);
            if (lastAccessTime != null && Objects.equals(lastAccessTime, entry.timestamp)) {
                iterator.remove();
                lastAccessTimeMap.remove(entry.uid);
                expiredUids.add(entry.uid);
            }
        }
        return expiredUids;
    }

    public boolean hasActiveUid(String uid) {
        return lastAccessTimeMap.containsKey(uid);
    }
}
