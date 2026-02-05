package com.example.paperhello.blockbreak;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Test;

class SpamFilterTest {
    @Test
    void broadcastsOncePerWindow() {
        AtomicLong now = new AtomicLong(0);
        SpamFilter filter = new SpamFilter(now::get);

        assertTrue(filter.recordAndShouldBroadcast("PlayerA"));
        assertFalse(filter.recordAndShouldBroadcast("PlayerA"));

        now.set(3001);
        assertTrue(filter.recordAndShouldBroadcast("PlayerA"));
        assertEquals(2, filter.getCountAndReset("PlayerA"));
    }
}
