package com.example.paperhello;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.jupiter.api.Test;

class JoinListenerTest {
    @Test
    void welcomesPlayerOnJoin() {
        Player player = mock(Player.class);
        PlayerJoinEvent event = new PlayerJoinEvent(player, "");

        JoinListener listener = new JoinListener();
        listener.onPlayerJoin(event);

        verify(player).sendMessage("Welcome to the server!");
    }
}
