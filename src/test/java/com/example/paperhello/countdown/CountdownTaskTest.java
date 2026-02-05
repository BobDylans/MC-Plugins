package com.example.paperhello.countdown;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;

class CountdownTaskTest {
    @Test
    void broadcastsCountdownAndGo() {
        List<String> messages = new ArrayList<>();
        AtomicBoolean completed = new AtomicBoolean(false);

        CountdownTask task = new CountdownTask(
            "PlayerA",
            3,
            messages::add,
            () -> completed.set(true)
        );

        task.run(); // 3
        task.run(); // 2
        task.run(); // 1
        task.run(); // GO!

        assertEquals(List.of("3", "2", "1", "GO!"), messages);
        assertTrue(completed.get());
    }
}
