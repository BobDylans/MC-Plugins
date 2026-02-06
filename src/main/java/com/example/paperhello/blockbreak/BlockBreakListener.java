package com.example.paperhello.blockbreak;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

// 实现一个监听器的接口,用于记录玩家破坏方块数量,并适当时候进行广播
public class BlockBreakListener implements Listener {
    private final SpamFilter spamFilter; // 这个是一个全屏的刷新器,防止有玩家刷屏
    private final StatsCleaner statsCleaner; //数据清理器
    //
    public BlockBreakListener(SpamFilter spamFilter, StatsCleaner statsCleaner) {
        this.spamFilter = spamFilter; 
        this.statsCleaner = statsCleaner;
    }

    // 注解中的参数代表的是时间的优先级,Monitor代表最后执行,只监听不修改
    // 这个注解本身是用来标注时间处理方法的
    @EventHandler(priority = EventPriority.MONITOR)
    // 传入的参数是一个event
    public void onBlockBreak(BlockBreakEvent event) {
        // 1.先获取用户的Name
        String playerName = event.getPlayer().getName();
        // 2.标记玩家为活跃状态
        statsCleaner.markActive(playerName);
        // 3. 
        if (spamFilter.recordAndShouldBroadcast(playerName)) {
            int count = spamFilter.getCountAndReset(playerName);
            Bukkit.broadcast(Component.text(playerName + " 破坏了 " + count + " 个方块"));
        }
    }
}
