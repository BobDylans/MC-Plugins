package com.example.paperhello.blockbreak;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {
    private final SpamFilter spamFilter;
    private final StatsCleaner statsCleaner;

    public BlockBreakListener(SpamFilter spamFilter, StatsCleaner statsCleaner) {
        this.spamFilter = spamFilter;
        this.statsCleaner = statsCleaner;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        String playerName = event.getPlayer().getName();
        statsCleaner.markActive(playerName);
        if (spamFilter.recordAndShouldBroadcast(playerName)) {
            int count = spamFilter.getCountAndReset(playerName);
            Bukkit.broadcast(Component.text(playerName + " 破坏了 " + count + " 个方块"));
        }
    }
}
