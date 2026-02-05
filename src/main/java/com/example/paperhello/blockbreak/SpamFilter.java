package com.example.paperhello.blockbreak;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.LongSupplier;

public class SpamFilter {
    private final ConcurrentHashMap<String, PlayerStats> playerStats = new ConcurrentHashMap<>();
    private final LongSupplier nowMillis;
    private final long windowMillis = 3000L;

    public SpamFilter(LongSupplier nowMillis) {
        this.nowMillis = nowMillis;
    }

    public boolean recordAndShouldBroadcast(String playerName) {
        PlayerStats stats = playerStats.computeIfAbsent(playerName, k -> new PlayerStats());
        stats.count.incrementAndGet();
        long now = nowMillis.getAsLong();
        if (!stats.hasBroadcasted) {
            stats.hasBroadcasted = true;
            stats.lastBroadcastTime = now;
            stats.lastBroadcastCount = stats.count.getAndSet(0);
            return true;
        }
        if (now - stats.lastBroadcastTime >= windowMillis) {
            stats.lastBroadcastTime = now;
            stats.lastBroadcastCount = stats.count.getAndSet(0);
            return true;
        }
        return false;
    }

    public int getCountAndReset(String playerName) {
        PlayerStats stats = playerStats.get(playerName);
        if (stats == null) {
            return 0;
        }
        int count = stats.lastBroadcastCount;
        stats.lastBroadcastCount = 0;
        return count;
    }

    static class PlayerStats {
        private final AtomicInteger count = new AtomicInteger(0);
        private volatile long lastBroadcastTime = 0L;
        private volatile int lastBroadcastCount = 0;
        private volatile boolean hasBroadcasted = false;
    }
}
