package com.example.paperhello.countdown;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class CountdownTask extends BukkitRunnable {
    private final AtomicInteger remainingSeconds;
    private final Consumer<String> broadcaster;
    private final Runnable onComplete;

    public CountdownTask(
            String playerName,
            int seconds,
            Consumer<String> broadcaster,
            Runnable onComplete
    ) {
        this.remainingSeconds = new AtomicInteger(seconds);
        this.broadcaster = broadcaster;
        this.onComplete = onComplete;
    }

    @Override
    public void run() {
        int current = remainingSeconds.getAndDecrement();
        if (current > 0) {
            broadcaster.accept(Integer.toString(current));
            return;
        }
        broadcaster.accept("GO!");
        onComplete.run();
        if (Bukkit.getServer() != null) {
            cancel();
        }
    }
}
