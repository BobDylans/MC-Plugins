package com.example.paperhello;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


// 主插件类,继承自JavaPlugin
public class PaperHelloPlugin extends JavaPlugin {
    // 创建一个ExecutorService,用于管理线程池
    private ExecutorService asyncExecutor;
    // 用于定时任务的线程池
    private ScheduledExecutorService scheduledExecutor;
    // 定时公告组件
    private ScheduledBroadcast scheduledBroadcast;
    private MainThreadExecutor mainThread;

    // 注意这两个方法,onEnable是插件启用时调用,onDisable是插件禁用时调用
    // 也就是用户刚刚进入时和退出时出发的方法不同
    // 当然也不局限于关闭,如果插件被禁用或者重载等,也会触发onDisable
    @Override
    public void onEnable() {
        // 创建一个固定大小的线程池,大小为2
        // 使用Executors下的静态方法创建一个线程池
        asyncExecutor = Executors.newFixedThreadPool(2);
        // 创建定时任务线程池
        scheduledExecutor = Executors.newScheduledThreadPool(1);
        // 这个task是一个runnable接口,相当于将这个task赋予给mainThread
        mainThread = task -> {
            getServer().getScheduler().runTask(this, task);
        };

        // 读取yml文件获取到对应的值
        if (getCommand("hello") != null) {
            // 获取到指令后指定执行器
            getCommand("hello").setExecutor(new HelloCommand(asyncExecutor, mainThread));
        }

        // 注册倒计时命令
        if (getCommand("countdown") != null) {
            getCommand("countdown").setExecutor(new CountdownCommand(scheduledExecutor, mainThread));
        }

        // 获取server,然后获取PluginManager,然后注册事件
        getServer().getPluginManager().registerEvents(new JoinListener(), this);

        // 启动定时公告
        scheduledBroadcast = new ScheduledBroadcast(getServer(), scheduledExecutor, mainThread);
        scheduledBroadcast.start();
    }

    @Override
    public void onDisable() {
        // 如果线程池未关闭,则将其关闭
        if (asyncExecutor != null) {
            asyncExecutor.shutdownNow();
        }
        // 关闭定时任务线程池
        if (scheduledExecutor != null) {
            scheduledExecutor.shutdownNow();
        }
    }
}
