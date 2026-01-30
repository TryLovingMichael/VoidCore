package dev.voidlessdev.voidCore.pearl;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PearlCommand implements CommandExecutor, TabCompleter {
    private final PearlCooldownManager cooldownManager;

    public PearlCommand(PearlCooldownManager cooldownManager) {
        this.cooldownManager = cooldownManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "toggle":
                return handleToggle(sender);
            case "setcooldown":
                return handleSetCooldown(sender, args);
            case "check":
                return handleCheck(sender);
            case "clear":
                return handleClear(sender, args);
            case "info":
                return handleInfo(sender);
            default:
                sendHelp(sender);
                return true;
        }
    }

    private boolean handleToggle(CommandSender sender) {
        if (!sender.hasPermission("voidcore.pearl.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        boolean newState = !cooldownManager.isEnabled();
        cooldownManager.setEnabled(newState);

        String status = newState ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled";
        sender.sendMessage(ChatColor.YELLOW + "Pearl cooldown has been " + status + ChatColor.YELLOW + ".");
        return true;
    }

    private boolean handleSetCooldown(CommandSender sender, String[] args) {
        if (!sender.hasPermission("voidcore.pearl.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /pearl setcooldown <seconds>");
            return true;
        }

        try {
            int seconds = Integer.parseInt(args[1]);
            if (seconds < 0) {
                sender.sendMessage(ChatColor.RED + "Cooldown must be a positive number.");
                return true;
            }

            cooldownManager.setCooldownTime(seconds);
            sender.sendMessage(ChatColor.GREEN + "Pearl cooldown set to " + seconds + " seconds.");
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid number: " + args[1]);
        }

        return true;
    }

    private boolean handleCheck(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (!cooldownManager.isEnabled()) {
            sender.sendMessage(ChatColor.YELLOW + "Pearl cooldown is currently disabled.");
            return true;
        }

        if (player.hasPermission("voidcore.pearl.bypass")) {
            sender.sendMessage(ChatColor.GREEN + "You have pearl cooldown bypass.");
            return true;
        }

        if (!cooldownManager.hasCooldown(player)) {
            sender.sendMessage(ChatColor.GREEN + "Your pearl is ready to use!");
            return true;
        }

        long remaining = cooldownManager.getRemainingCooldown(player);
        sender.sendMessage(ChatColor.YELLOW + "Pearl cooldown: " + ChatColor.RED + remaining + " seconds");
        return true;
    }

    private boolean handleClear(CommandSender sender, String[] args) {
        if (!sender.hasPermission("voidcore.pearl.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length < 2) {
            // Clear all cooldowns
            cooldownManager.clearAllCooldowns();
            sender.sendMessage(ChatColor.GREEN + "All pearl cooldowns have been cleared.");
            return true;
        }

        // Clear specific player's cooldown
        Player target = sender.getServer().getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found: " + args[1]);
            return true;
        }

        cooldownManager.removeCooldown(target);
        sender.sendMessage(ChatColor.GREEN + "Cleared pearl cooldown for " + target.getName() + ".");
        target.sendMessage(ChatColor.GREEN + "Your pearl cooldown has been cleared by an administrator.");
        return true;
    }

    private boolean handleInfo(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== Pearl Cooldown Info ===");
        sender.sendMessage(ChatColor.YELLOW + "Enabled: " + (cooldownManager.isEnabled() ?
                ChatColor.GREEN + "Yes" : ChatColor.RED + "No"));
        sender.sendMessage(ChatColor.YELLOW + "Cooldown Time: " + ChatColor.WHITE +
                cooldownManager.getCooldownTime() + " seconds");

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("voidcore.pearl.bypass")) {
                sender.sendMessage(ChatColor.YELLOW + "Status: " + ChatColor.GREEN + "Bypass Active");
            } else if (cooldownManager.hasCooldown(player)) {
                long remaining = cooldownManager.getRemainingCooldown(player);
                sender.sendMessage(ChatColor.YELLOW + "Your Cooldown: " + ChatColor.RED + remaining + "s");
            } else {
                sender.sendMessage(ChatColor.YELLOW + "Your Cooldown: " + ChatColor.GREEN + "Ready");
            }
        }

        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== Pearl Commands ===");
        sender.sendMessage(ChatColor.YELLOW + "/pearl check" + ChatColor.WHITE + " - Check your pearl cooldown");
        sender.sendMessage(ChatColor.YELLOW + "/pearl info" + ChatColor.WHITE + " - View pearl cooldown information");

        if (sender.hasPermission("voidcore.pearl.admin")) {
            sender.sendMessage(ChatColor.YELLOW + "/pearl toggle" + ChatColor.WHITE + " - Toggle pearl cooldown on/off");
            sender.sendMessage(ChatColor.YELLOW + "/pearl setcooldown <seconds>" + ChatColor.WHITE + " - Set cooldown time");
            sender.sendMessage(ChatColor.YELLOW + "/pearl clear [player]" + ChatColor.WHITE + " - Clear cooldown(s)");
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            List<String> options = Arrays.asList("check", "info");
            if (sender.hasPermission("voidcore.pearl.admin")) {
                options = Arrays.asList("check", "info", "toggle", "setcooldown", "clear");
            }

            for (String option : options) {
                if (option.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(option);
                }
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("clear") && sender.hasPermission("voidcore.pearl.admin")) {
                // Add online player names
                for (Player player : sender.getServer().getOnlinePlayers()) {
                    if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                        completions.add(player.getName());
                    }
                }
            } else if (args[0].equalsIgnoreCase("setcooldown") && sender.hasPermission("voidcore.pearl.admin")) {
                // Suggest common cooldown values
                completions.addAll(Arrays.asList("5", "10", "15", "20", "30", "60"));
            }
        }

        return completions;
    }
}

