package dev.voidlessdev.voidCore.pearl;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PearlCooldownManager {
    private final Plugin plugin;
    private final Map<UUID, Long> cooldowns;
    private boolean enabled;
    private int cooldownTime;
    private String cooldownMessage;
    private String readyMessage;
    private boolean actionbarEnabled;
    private String bypassPermission;

    public PearlCooldownManager(Plugin plugin) {
        this.plugin = plugin;
        this.cooldowns = new HashMap<>();
        loadConfig();
    }

    public void loadConfig() {
        this.enabled = plugin.getConfig().getBoolean("pearl-cooldown.enabled", true);
        this.cooldownTime = plugin.getConfig().getInt("pearl-cooldown.cooldown", 15);
        this.cooldownMessage = ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("pearl-cooldown.cooldown-message",
                        "&cYou must wait {time} seconds before using another ender pearl!"));
        this.readyMessage = ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("pearl-cooldown.ready-message",
                        "&aYour ender pearl is ready!"));
        this.actionbarEnabled = plugin.getConfig().getBoolean("pearl-cooldown.actionbar-enabled", true);
        this.bypassPermission = plugin.getConfig().getString("pearl-cooldown.bypass-permission",
                "voidcore.pearl.bypass");
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        plugin.getConfig().set("pearl-cooldown.enabled", enabled);
        plugin.saveConfig();
    }

    public int getCooldownTime() {
        return cooldownTime;
    }

    public void setCooldownTime(int seconds) {
        this.cooldownTime = seconds;
        plugin.getConfig().set("pearl-cooldown.cooldown", seconds);
        plugin.saveConfig();
    }

    public boolean hasCooldown(Player player) {
        if (!enabled || player.hasPermission(bypassPermission)) {
            return false;
        }

        Long cooldownEnd = cooldowns.get(player.getUniqueId());
        if (cooldownEnd == null) {
            return false;
        }

        return System.currentTimeMillis() < cooldownEnd;
    }

    public long getRemainingCooldown(Player player) {
        Long cooldownEnd = cooldowns.get(player.getUniqueId());
        if (cooldownEnd == null) {
            return 0;
        }

        long remaining = cooldownEnd - System.currentTimeMillis();
        return remaining > 0 ? remaining / 1000 : 0;
    }

    public void applyCooldown(Player player) {
        if (!enabled || player.hasPermission(bypassPermission)) {
            return;
        }

        long cooldownEnd = System.currentTimeMillis() + (cooldownTime * 1000L);
        cooldowns.put(player.getUniqueId(), cooldownEnd);

        if (actionbarEnabled) {
            startActionbarCountdown(player, cooldownTime);
        }
    }

    public void sendCooldownMessage(Player player) {
        long remaining = getRemainingCooldown(player);
        String message = cooldownMessage.replace("{time}", String.valueOf(remaining));
        player.sendMessage(message);
    }

    public void removeCooldown(Player player) {
        cooldowns.remove(player.getUniqueId());
    }

    public void clearAllCooldowns() {
        cooldowns.clear();
    }

    private void startActionbarCountdown(Player player, int seconds) {
        new BukkitRunnable() {
            int remaining = seconds;

            @Override
            public void run() {
                if (!player.isOnline() || remaining <= 0) {
                    if (player.isOnline() && remaining <= 0) {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                TextComponent.fromLegacyText(readyMessage));
                    }
                    cancel();
                    return;
                }

                // Check if cooldown was manually removed
                if (!hasCooldown(player)) {
                    cancel();
                    return;
                }

                String message = ChatColor.RED + "Pearl Cooldown: " + ChatColor.YELLOW + remaining + "s";
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        TextComponent.fromLegacyText(message));

                remaining--;
            }
        }.runTaskTimer(plugin, 0L, 20L); // Run every second
    }
}

