package com.example.paperhello.countdown;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CountdownCommand implements CommandExecutor {
    private final CountdownManager manager;

    public CountdownCommand(CountdownManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("Usage: /countdown <seconds>");
            return true;
        }
        int seconds;
        try {
            seconds = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage("Seconds must be a number");
            return true;
        }
        if (seconds <= 0) {
            sender.sendMessage("Seconds must be > 0");
            return true;
        }
        boolean started = manager.tryStartCountdown(sender.getName(), seconds);
        if (!started) {
            sender.sendMessage("You already have an active countdown");
        }
        return true;
    }
}
