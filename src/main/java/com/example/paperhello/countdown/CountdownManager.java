package com.example.paperhello.countdown;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class CountdownManager {
    private final ConcurrentHashMap<Integer, CountdownTask> activeTasks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Integer> playerToTaskId = new ConcurrentHashMap<>();
    private final AtomicInteger taskIdGenerator = new AtomicInteger(1);
    private final Consumer<CountdownTask> scheduler;
    private final Runnable onTaskComplete;

    public CountdownManager(Consumer<CountdownTask> scheduler, Runnable onTaskComplete) {
        this.scheduler = scheduler;
        this.onTaskComplete = onTaskComplete;
    }

    public boolean tryStartCountdown(String playerName, int seconds) {
        if (playerToTaskId.containsKey(playerName)) {
            return false;
        }
        int taskId = taskIdGenerator.getAndIncrement();
        CountdownTask task = new CountdownTask(
            playerName,
            seconds,
            msg -> {},
            () -> complete(taskId, playerName)
        );
        playerToTaskId.put(playerName, taskId);
        activeTasks.put(taskId, task);
        scheduler.accept(task);
        return true;
    }

    private void complete(int taskId, String playerName) {
        activeTasks.remove(taskId);
        playerToTaskId.remove(playerName);
        onTaskComplete.run();
    }
}
