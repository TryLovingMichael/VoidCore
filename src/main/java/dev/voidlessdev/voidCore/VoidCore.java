package dev.voidlessdev.voidCore;

import dev.voidlessdev.voidCore.command.VoidCoreCommand;
import dev.voidlessdev.voidCore.pearl.PearlCommand;
import dev.voidlessdev.voidCore.pearl.PearlCooldownListener;
import dev.voidlessdev.voidCore.pearl.PearlCooldownManager;
import dev.voidlessdev.voidCore.placeholder.VoidCorePlaceholder;
import dev.voidlessdev.voidCore.team.TeamCommand;
import dev.voidlessdev.voidCore.team.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class VoidCore extends JavaPlugin {
    private TeamManager teamManager;
    private PearlCooldownManager pearlCooldownManager;

    @Override
    public void onEnable() {
        // Save default config
        saveDefaultConfig();

        // Initialize managers
        teamManager = new TeamManager(this);
        pearlCooldownManager = new PearlCooldownManager(this);

        // Register team commands
        TeamCommand teamCommand = new TeamCommand(this, teamManager);
        getCommand("team").setExecutor(teamCommand);
        getCommand("team").setTabCompleter(teamCommand);

        // Register pearl commands
        PearlCommand pearlCommand = new PearlCommand(pearlCooldownManager);
        getCommand("pearl").setExecutor(pearlCommand);
        getCommand("pearl").setTabCompleter(pearlCommand);

        // Register VoidCore command
        VoidCoreCommand voidCoreCommand = new VoidCoreCommand(this);
        getCommand("voidcore").setExecutor(voidCoreCommand);

        // Register listeners
        getServer().getPluginManager().registerEvents(new PearlCooldownListener(pearlCooldownManager), this);

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

    public void reloadManagers() {
        // Reload pearl cooldown manager
        if (pearlCooldownManager != null) {
            pearlCooldownManager.loadConfig();
        }
        getLogger().info("Managers reloaded successfully!");
    }

    public TeamManager getTeamManager() {
        return teamManager;
    }

    public PearlCooldownManager getPearlCooldownManager() {
        return pearlCooldownManager;
    }
}
