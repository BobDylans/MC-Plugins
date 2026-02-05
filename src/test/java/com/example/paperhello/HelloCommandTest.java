package com.example.paperhello;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.Test;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class HelloCommandTest {
    @Test
    void sendsHelloMessageOnMainThread() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        TestMainThreadExecutor mainThread = new TestMainThreadExecutor(1);
        HelloCommand cmd = new HelloCommand(executor, mainThread);
        CommandSender sender = mock(CommandSender.class);
        Command command = mock(Command.class);

        boolean result = cmd.onCommand(sender, command, "hello", new String[0]);

        assertTrue(result);
        assertTrue(mainThread.awaitRuns(1, TimeUnit.SECONDS));
        verify(sender, never()).sendMessage(anyString());
        mainThread.runAll();
        verify(sender).sendMessage("你好!");
        verify(sender).sendMessage("Hello from PaperHello!");
        executor.shutdownNow();
    }
}
