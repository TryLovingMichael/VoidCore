package dev.voidlessdev.voidCore.command;

import dev.voidlessdev.voidCore.VoidCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class VoidCoreCommand implements CommandExecutor {
    private final VoidCore plugin;

    public VoidCoreCommand(VoidCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            return handleReload(sender);
        } else if (args[0].equalsIgnoreCase("version") || args[0].equalsIgnoreCase("ver")) {
            return handleVersion(sender);
        } else {
            sendHelp(sender);
            return true;
        }
    }

    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("voidcore.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        try {
            // Reload config
            plugin.reloadConfig();
            plugin.reloadManagers();

            sender.sendMessage(ChatColor.GREEN + "VoidCore configuration has been reloaded!");
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "An error occurred while reloading the configuration.");
            plugin.getLogger().severe("Error reloading config: " + e.getMessage());
            e.printStackTrace();
        }

        return true;
    }

    private boolean handleVersion(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== VoidCore ===");
        sender.sendMessage(ChatColor.YELLOW + "Version: " + ChatColor.WHITE + plugin.getDescription().getVersion());
        sender.sendMessage(ChatColor.YELLOW + "Author: " + ChatColor.WHITE + "VoidlessDev");
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== VoidCore Commands ===");
        sender.sendMessage(ChatColor.YELLOW + "/voidcore version" + ChatColor.WHITE + " - Show plugin version");

        if (sender.hasPermission("voidcore.admin")) {
            sender.sendMessage(ChatColor.YELLOW + "/voidcore reload" + ChatColor.WHITE + " - Reload configuration");
        }
    }
}

