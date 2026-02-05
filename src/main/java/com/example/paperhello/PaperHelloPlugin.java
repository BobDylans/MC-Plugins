package com.example.paperhello;

import com.example.paperhello.blockbreak.BlockBreakListener;
import com.example.paperhello.blockbreak.SpamFilter;
import com.example.paperhello.blockbreak.StatsCleaner;
import com.example.paperhello.countdown.CountdownCommand;
import com.example.paperhello.countdown.CountdownManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


// 主插件类,继承自JavaPlugin
public class PaperHelloPlugin extends JavaPlugin {
    // 创建一个ExecutorService,用于管理线程池
    private ExecutorService asyncExecutor;
    private StatsCleaner statsCleaner;

    // 注意这两个方法,onEnable是插件启用时调用,onDisable是插件禁用时调用
    // 也就是用户刚刚进入时和退出时出发的方法不同
    // 当然也不局限于关闭,如果插件被禁用或者重载等,也会触发onDisable
    @Override
    public void onEnable() {
        // 创建一个固定大小的线程池,大小为2
        // 使用Executors下的静态方法创建一个线程池
        asyncExecutor = Executors.newFixedThreadPool(2);
        // 读取yml文件获取到对应的值
        if (getCommand("hello") != null) {
            // 获取到指令后指定执行器
            getCommand("hello").setExecutor(new HelloCommand(asyncExecutor));
        }

        CountdownManager countdownManager = new CountdownManager(
            task -> task.runTaskTimer(this, 0L, 20L),
            () -> {},
            message -> Bukkit.broadcast(Component.text(message))
        );
        if (getCommand("countdown") != null) {
            getCommand("countdown").setExecutor(new CountdownCommand(countdownManager));
        }

        SpamFilter spamFilter = new SpamFilter(System::currentTimeMillis);
        statsCleaner = new StatsCleaner(spamFilter, System::currentTimeMillis);
        statsCleaner.startCleanup();

        // 获取server,然后获取PluginManager,然后注册事件
        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(spamFilter, statsCleaner), this);
    }

    @Override
    public void onDisable() {
        // 如果线程池未关闭,则将其关闭
        if (asyncExecutor != null) {
            asyncExecutor.shutdownNow();
        }
        if (statsCleaner != null) {
            statsCleaner.shutdown();
        }
    }
}
