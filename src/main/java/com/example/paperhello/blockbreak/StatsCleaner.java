package com.example.paperhello.blockbreak;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.LongSupplier;

public class StatsCleaner {
    private final SpamFilter filter;
    private final LongSupplier nowMillis;
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final Map<String, Long> lastSeen = new ConcurrentHashMap<>();
    private final long staleMillis = 60_000L;

    public StatsCleaner(SpamFilter filter, LongSupplier nowMillis) {
        this.filter = filter;
        this.nowMillis = nowMillis;
    }

    public void markActive(String playerName) {
        lastSeen.put(playerName, nowMillis.getAsLong());
    }

    public boolean isActive(String playerName) {
        return lastSeen.containsKey(playerName);
    }

    public void startCleanup() {
        executor.scheduleAtFixedRate(this::runCleanup, 60, 60, TimeUnit.SECONDS);
    }

    public void runCleanup() {
        long now = nowMillis.getAsLong();
        lastSeen.entrySet().removeIf(e -> now - e.getValue() > staleMillis);
    }

    public void shutdown() {
        executor.shutdownNow();
    }
}
