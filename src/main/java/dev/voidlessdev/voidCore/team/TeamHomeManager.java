package dev.voidlessdev.voidCore.team;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeamHomeManager {
    private final Plugin plugin;
    private final Map<UUID, Long> homeCooldowns;

    public TeamHomeManager(Plugin plugin) {
        this.plugin = plugin;
        this.homeCooldowns = new HashMap<>();
    }

    public boolean hasCooldown(Player player) {
        Long cooldownEnd = homeCooldowns.get(player.getUniqueId());
        if (cooldownEnd == null) {
            return false;
        }

        return System.currentTimeMillis() < cooldownEnd;
    }

    public long getRemainingCooldown(Player player) {
        Long cooldownEnd = homeCooldowns.get(player.getUniqueId());
        if (cooldownEnd == null) {
            return 0;
        }

        long remaining = cooldownEnd - System.currentTimeMillis();
        return remaining > 0 ? remaining / 1000 : 0;
    }

    public void applyCooldown(Player player) {
        int cooldownSeconds = plugin.getConfig().getInt("team-home.home-cooldown", 60);
        long cooldownEnd = System.currentTimeMillis() + (cooldownSeconds * 1000L);
        homeCooldowns.put(player.getUniqueId(), cooldownEnd);
    }

    public void removeCooldown(Player player) {
        homeCooldowns.remove(player.getUniqueId());
    }

    public void clearAllCooldowns() {
        homeCooldowns.clear();
    }
}

