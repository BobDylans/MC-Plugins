package com.example.paperhello;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.Test;

class HelloCommandTest {
    @Test
    void sendsHelloMessage() {
        HelloCommand cmd = new HelloCommand();
        CommandSender sender = mock(CommandSender.class);
        Command command = mock(Command.class);

        boolean result = cmd.onCommand(sender, command, "hello", new String[0]);

        assertTrue(result);
        verify(sender).sendMessage("Hello from PaperHello!");
    }
}
