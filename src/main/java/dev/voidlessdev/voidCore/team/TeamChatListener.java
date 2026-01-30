package dev.voidlessdev.voidCore.team;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class TeamChatListener implements Listener {
    private final TeamChatManager chatManager;

    public TeamChatListener(TeamChatManager chatManager) {
        this.chatManager = chatManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        // Check if player is in team chat mode
        if (chatManager.isInTeamChatMode(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            chatManager.sendTeamMessage(event.getPlayer(), event.getMessage());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Clean up when player leaves
        chatManager.removePlayer(event.getPlayer().getUniqueId());
    }
}

