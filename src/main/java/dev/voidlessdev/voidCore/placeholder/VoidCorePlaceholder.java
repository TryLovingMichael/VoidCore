package dev.voidlessdev.voidCore.placeholder;

import dev.voidlessdev.voidCore.VoidCore;
import dev.voidlessdev.voidCore.team.Team;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VoidCorePlaceholder extends PlaceholderExpansion {
    private final VoidCore plugin;

    public VoidCorePlaceholder(VoidCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "void";
    }

    @Override
    public @NotNull String getAuthor() {
        return "VoidlessDev";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) {
            return "";
        }

        // %void_team%
        if (params.equalsIgnoreCase("team")) {
            Team team = plugin.getTeamManager().getPlayerTeam(player.getUniqueId());
            return team != null ? team.getName() : "";
        }

        // %void_team_owner%
        if (params.equalsIgnoreCase("team_owner")) {
            Team team = plugin.getTeamManager().getPlayerTeam(player.getUniqueId());
            if (team != null) {
                OfflinePlayer owner = plugin.getServer().getOfflinePlayer(team.getOwner());
                return owner.getName() != null ? owner.getName() : "Unknown";
            }
            return "";
        }

        // %void_team_members%
        if (params.equalsIgnoreCase("team_members")) {
            Team team = plugin.getTeamManager().getPlayerTeam(player.getUniqueId());
            return team != null ? String.valueOf(team.getMemberCount()) : "0";
        }

        // %void_has_team%
        if (params.equalsIgnoreCase("has_team")) {
            Team team = plugin.getTeamManager().getPlayerTeam(player.getUniqueId());
            return team != null ? "true" : "false";
        }

        // %void_is_team_owner%
        if (params.equalsIgnoreCase("is_team_owner")) {
            Team team = plugin.getTeamManager().getPlayerTeam(player.getUniqueId());
            return team != null && team.isOwner(player.getUniqueId()) ? "true" : "false";
        }

        // %void_team_color%
        if (params.equalsIgnoreCase("team_color")) {
            Team team = plugin.getTeamManager().getPlayerTeam(player.getUniqueId());
            return team != null ? team.getColor() : "";
        }

        // %void_team_colored% - Returns team name with legacy hex format (most compatible)
        if (params.equalsIgnoreCase("team_colored")) {
            Team team = plugin.getTeamManager().getPlayerTeam(player.getUniqueId());
            if (team != null) {
                // Use &x format for legacy hex colors (works with most plugins)
                String hexColor = team.getColor().replace("#", "");
                StringBuilder colorCode = new StringBuilder("&x");
                for (char c : hexColor.toCharArray()) {
                    colorCode.append("&").append(c);
                }
                return colorCode + team.getName();
            }
            return "";
        }

        // %void_team_colored_mini% - Returns team name with MiniMessage format
        if (params.equalsIgnoreCase("team_colored_mini")) {
            Team team = plugin.getTeamManager().getPlayerTeam(player.getUniqueId());
            if (team != null) {
                return "<color:" + team.getColor() + ">" + team.getName() + "</color>";
            }
            return "";
        }

        // %void_team_colored_hex% - Returns team name with &#RRGGBB format
        if (params.equalsIgnoreCase("team_colored_hex")) {
            Team team = plugin.getTeamManager().getPlayerTeam(player.getUniqueId());
            if (team != null) {
                return "&" + team.getColor() + team.getName();
            }
            return "";
        }

        // Pearl cooldown placeholders
        // %void_pearl_cooldown% - Returns remaining cooldown in seconds
        if (params.equalsIgnoreCase("pearl_cooldown")) {
            if (player.isOnline() && player.getPlayer() != null) {
                long remaining = plugin.getPearlCooldownManager().getRemainingCooldown(player.getPlayer());
                return String.valueOf(remaining);
            }
            return "0";
        }

        // %void_pearl_ready% - Returns true/false if pearl is ready
        if (params.equalsIgnoreCase("pearl_ready")) {
            if (player.isOnline() && player.getPlayer() != null) {
                boolean hasCooldown = plugin.getPearlCooldownManager().hasCooldown(player.getPlayer());
                return hasCooldown ? "false" : "true";
            }
            return "true";
        }

        // %void_pearl_cooldown_enabled% - Returns true/false if cooldown system is enabled
        if (params.equalsIgnoreCase("pearl_cooldown_enabled")) {
            return plugin.getPearlCooldownManager().isEnabled() ? "true" : "false";
        }

        return null;
    }
}

