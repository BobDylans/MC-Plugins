package com.example.paperhello.countdown;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;

class CountdownManagerTest {
    @Test
    void preventsDuplicateCountdownPerPlayer() {
        AtomicBoolean completed = new AtomicBoolean(false);
        CountdownManager manager = new CountdownManager(
            task -> {},
            () -> completed.set(true)
        );

        assertTrue(manager.tryStartCountdown("PlayerA", 3));
        assertFalse(manager.tryStartCountdown("PlayerA", 5));
    }
}
