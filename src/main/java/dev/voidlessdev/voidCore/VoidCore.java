package dev.voidlessdev.voidCore;

import dev.voidlessdev.voidCore.placeholder.VoidCorePlaceholder;
import dev.voidlessdev.voidCore.team.TeamCommand;
import dev.voidlessdev.voidCore.team.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class VoidCore extends JavaPlugin {
    private TeamManager teamManager;

    @Override
    public void onEnable() {
        // Initialize team manager
        teamManager = new TeamManager(this);

        // Register commands
        TeamCommand teamCommand = new TeamCommand(this, teamManager);
        getCommand("team").setExecutor(teamCommand);
        getCommand("team").setTabCompleter(teamCommand);

        // Hook into PlaceholderAPI if available
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new VoidCorePlaceholder(this).register();
            getLogger().info("Successfully hooked into PlaceholderAPI!");
        } else {
            getLogger().warning("PlaceholderAPI not found! Placeholder %void_team% will not work.");
        }

        getLogger().info("VoidCore has been enabled!");
    }

    @Override
    public void onDisable() {
        // Save teams before shutdown
        if (teamManager != null) {
            teamManager.saveTeams();
        }

        getLogger().info("VoidCore has been disabled!");
    }

    public TeamManager getTeamManager() {
        return teamManager;
    }
}
