package com.example.paperhello.blockbreak;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Test;

class StatsCleanerTest {
    @Test
    void removesInactivePlayers() {
        AtomicLong now = new AtomicLong(0);
        SpamFilter filter = new SpamFilter(now::get);
        filter.recordAndShouldBroadcast("PlayerA");

        StatsCleaner cleaner = new StatsCleaner(filter, now::get);
        cleaner.markActive("PlayerA");

        now.set(120_000);
        cleaner.runCleanup();

        assertFalse(cleaner.isActive("PlayerA"));
    }
}
