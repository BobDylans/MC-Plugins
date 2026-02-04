package com.example.paperhello;

import org.bukkit.plugin.java.JavaPlugin;


// 主插件类,继承自JavaPlugin
public class PaperHelloPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        if (getCommand("hello") != null) {
            getCommand("hello").setExecutor(new HelloCommand());
        }
        getServer().getPluginManager().registerEvents(new JoinListener(), this);
    }
}
