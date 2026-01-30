package dev.voidlessdev.voidCore.team;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class TeamChatCommand implements CommandExecutor {
    private final TeamManager teamManager;
    private final TeamChatManager chatManager;

    public TeamChatCommand(TeamManager teamManager, TeamChatManager chatManager) {
        this.teamManager = teamManager;
        this.chatManager = chatManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command!", NamedTextColor.RED));
            return true;
        }

        Team team = teamManager.getPlayerTeam(player.getUniqueId());

        if (team == null) {
            player.sendMessage(Component.text("You are not in a team!", NamedTextColor.RED));
            return true;
        }

        // If no args, it's a quick message usage error
        if (args.length == 0) {
            player.sendMessage(Component.text("Usage: /tc <message>", NamedTextColor.RED));
            player.sendMessage(Component.text("Or use /team chat to toggle team chat mode", NamedTextColor.YELLOW));
            return true;
        }

        // Send the message to team
        String message = String.join(" ", args);
        chatManager.sendTeamMessage(player, message);

        return true;
    }
}

