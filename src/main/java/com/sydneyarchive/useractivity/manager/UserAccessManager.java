package com.sydneyarchive.useractivity.manager;

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

    record AccessEntry(long timestamp, String sid) implements Comparable<AccessEntry> {

        @Override
            public int compareTo(@NotNull AccessEntry o) {
                int cmp = Long.compare(this.timestamp, o.timestamp);
                if (cmp != 0) return cmp;
                return this.sid.compareTo(o.sid);
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof AccessEntry(long timestamp, String sid))) return false;
                return this.timestamp == timestamp && this.sid.equals(sid);
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

    private LockWithIndex getLock(String sid) {
        int index = sid.hashCode() & (STRIPE_COUNT - 1);
        return new LockWithIndex(locks[index], index);
    }

    public void recordAccess(String sid) {
        LockWithIndex lock = getLock(sid);
        lock.lock();
        try {
            log.info("[recordAccess] sid='{}' acquired lock index={}", sid, lock.index);

            long now = System.currentTimeMillis();
            Long previousTimestamp = lastAccessTimeMap.get(sid);
            if (previousTimestamp != null && previousTimestamp > now) {
                return;
            }

            if (previousTimestamp != null) {
                accessSortedSet.remove(new AccessEntry(previousTimestamp, sid));
            }

            lastAccessTimeMap.put(sid, now);
            accessSortedSet.add(new AccessEntry(now, sid));
        } finally {
            lock.unlock();
            log.info("[recordAccess] sid='{}' released lock index={}", sid, lock.index);
        }
    }

    public List<String> removeExpiredSids(long cutoff) {
        List<String> expiredSids = new ArrayList<>();
        Iterator<AccessEntry> iterator = accessSortedSet.iterator();
        while (iterator.hasNext()) {
            AccessEntry entry = iterator.next();
            if (entry.timestamp > cutoff) {
                break;
            }

            Long lastAccessTime = lastAccessTimeMap.get(entry.sid);
            if (lastAccessTime != null && Objects.equals(lastAccessTime, entry.timestamp)) {
                iterator.remove();
                lastAccessTimeMap.remove(entry.sid);
                expiredSids.add(entry.sid);
            }
        }
        return expiredSids;
    }

    public boolean hasActiveSid(String sid) {
        return lastAccessTimeMap.containsKey(sid);
    }
}
