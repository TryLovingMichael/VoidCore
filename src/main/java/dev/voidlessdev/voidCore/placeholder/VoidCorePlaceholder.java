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

        // %void_team_colored% - Returns the team name with its hex color applied
        if (params.equalsIgnoreCase("team_colored")) {
            Team team = plugin.getTeamManager().getPlayerTeam(player.getUniqueId());
            if (team != null) {
                return "<" + team.getColor() + ">" + team.getName();
            }
            return "";
        }

        return null;
    }
}

