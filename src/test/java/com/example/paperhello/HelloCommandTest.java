package com.example.paperhello;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.Test;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class HelloCommandTest {
    @Test
    void sendsHelloMessageAsync() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        HelloCommand cmd = new HelloCommand(executor);
        CommandSender sender = mock(CommandSender.class);
        Command command = mock(Command.class);
        CountDownLatch latch = new CountDownLatch(1);

        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(sender).sendMessage("Hello from PaperHello!");

        boolean result = cmd.onCommand(sender, command, "hello", new String[0]);

        assertTrue(result);
        assertTrue(latch.await(1, TimeUnit.SECONDS));
        verify(sender).sendMessage("Hello from PaperHello!");
        executor.shutdownNow();
    }
}
