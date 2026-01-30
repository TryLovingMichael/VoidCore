package dev.voidlessdev.voidCore.pearl;

import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public class PearlCooldownListener implements Listener {
    private final PearlCooldownManager cooldownManager;

    public PearlCooldownListener(PearlCooldownManager cooldownManager) {
        this.cooldownManager = cooldownManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPearlThrow(ProjectileLaunchEvent event) {
        // Check if it's an ender pearl
        if (!(event.getEntity() instanceof EnderPearl)) {
            return;
        }

        // Check if thrown by a player
        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity().getShooter();

        // Check if player has cooldown
        if (cooldownManager.hasCooldown(player)) {
            event.setCancelled(true);
            cooldownManager.sendCooldownMessage(player);
            return;
        }

        // Apply cooldown only when pearl is actually thrown
        cooldownManager.applyCooldown(player);
    }
}

