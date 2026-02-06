package com.example.paperhello.blockbreak;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.LongSupplier;
// 这个类的重点含义是防止刷屏,给出3秒的缓冲时间
public class SpamFilter {
    
    private final ConcurrentHashMap<String, PlayerStats> playerStats = new ConcurrentHashMap<>();
    private final LongSupplier nowMillis;
    // 广播窗口是3秒
    private final long windowMillis = 3000L;

    public SpamFilter(LongSupplier nowMillis) {
        this.nowMillis = nowMillis;
    }

    public boolean recordAndShouldBroadcast(String playerName) {
        // 1.获取或者创建玩家的统计数据
        PlayerStats stats = playerStats.computeIfAbsent(playerName, k -> new PlayerStats());
        stats.count.incrementAndGet();
        long now = nowMillis.getAsLong();
        // 如果是第一次破坏方块
        if (!stats.hasBroadcasted) {
            stats.hasBroadcasted = true;
            stats.lastBroadcastTime = now;
            stats.lastBroadcastCount = stats.count.getAndSet(0);
            return true;
        }
        // 如果和上次破坏方块的时间间隔超过3秒
        if (now - stats.lastBroadcastTime >= windowMillis) {
            stats.lastBroadcastTime = now;
            stats.lastBroadcastCount = stats.count.getAndSet(0);
            return true;
        }
        // 若是和上次间隔时间不足3秒则不广播
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
