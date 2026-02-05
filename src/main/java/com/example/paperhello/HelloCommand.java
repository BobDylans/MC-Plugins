package com.example.paperhello;

import java.util.concurrent.ExecutorService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HelloCommand implements CommandExecutor {
    // 这里创建了一个线程池,当接收到指令时会从线程池中获取一个来执行具体的任务
    private final ExecutorService executor;
    private final MainThreadExecutor mainThread;

    // 构造方法中加入这个executor方便直接执行
    public HelloCommand(ExecutorService executor, MainThreadExecutor mainThread) {
        this.executor = executor;
        this.mainThread = mainThread;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // 这里的execute方法只有一个参数,是一个Runnable接口,直接使用lambda表达式完善即可
        executor.execute(() -> mainThread.run(() -> {
            sender.sendMessage("你好!");
            sender.sendMessage("Hello from PaperHello!");
        }));
        return true;
    }
}
