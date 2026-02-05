package com.example.paperhello;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    // 这个注解标注这个方法是自动处理函数,Bukkit事件系统会自动识别并触发
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // 这个都是固定好的方法
        event.getPlayer().sendMessage("Welcome to the server!");
    }
}
