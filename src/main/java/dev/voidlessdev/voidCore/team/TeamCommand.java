package dev.voidlessdev.voidCore.team;

import dev.voidlessdev.voidCore.VoidCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class TeamCommand implements CommandExecutor, TabCompleter {
    private final VoidCore plugin;
    private final TeamManager teamManager;

    public TeamCommand(VoidCore plugin, TeamManager teamManager) {
        this.plugin = plugin;
        this.teamManager = teamManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command!", NamedTextColor.RED));
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "create" -> handleCreate(player, args);
            case "disband" -> handleDisband(player);
            case "invite" -> handleInvite(player, args);
            case "kick" -> handleKick(player, args);
            case "leave" -> handleLeave(player);
            case "accept" -> handleAccept(player, args);
            case "deny" -> handleDeny(player, args);
            case "info" -> handleInfo(player, args);
            case "list" -> handleList(player);
            default -> sendHelp(player);
        }

        return true;
    }

    private void handleCreate(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Component.text("Usage: /team create <name>", NamedTextColor.RED));
            return;
        }

        String teamName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        if (teamManager.getPlayerTeam(player.getUniqueId()) != null) {
            player.sendMessage(Component.text("You are already in a team!", NamedTextColor.RED));
            return;
        }

        if (teamManager.isTeamNameTaken(teamName)) {
            player.sendMessage(Component.text("A team with that name already exists!", NamedTextColor.RED));
            return;
        }

        Team team = teamManager.createTeam(teamName, player.getUniqueId());
        if (team != null) {
            player.sendMessage(Component.text("Successfully created team: ", NamedTextColor.GREEN)
                    .append(Component.text(teamName, NamedTextColor.GOLD, TextDecoration.BOLD)));
        } else {
            player.sendMessage(Component.text("Failed to create team!", NamedTextColor.RED));
        }
    }

    private void handleDisband(Player player) {
        Team team = teamManager.getPlayerTeam(player.getUniqueId());

        if (team == null) {
            player.sendMessage(Component.text("You are not in a team!", NamedTextColor.RED));
            return;
        }

        if (!team.isOwner(player.getUniqueId())) {
            player.sendMessage(Component.text("Only the team owner can disband the team!", NamedTextColor.RED));
            return;
        }

        String teamName = team.getName();
        Set<UUID> members = team.getMembers();

        if (teamManager.disbandTeam(team.getId())) {
            player.sendMessage(Component.text("Successfully disbanded team: ", NamedTextColor.GREEN)
                    .append(Component.text(teamName, NamedTextColor.GOLD)));

            // Notify all members
            for (UUID memberId : members) {
                if (!memberId.equals(player.getUniqueId())) {
                    Player member = Bukkit.getPlayer(memberId);
                    if (member != null && member.isOnline()) {
                        member.sendMessage(Component.text("Your team has been disbanded by the owner!", NamedTextColor.YELLOW));
                    }
                }
            }
        } else {
            player.sendMessage(Component.text("Failed to disband team!", NamedTextColor.RED));
        }
    }

    private void handleInvite(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Component.text("Usage: /team invite <player>", NamedTextColor.RED));
            return;
        }

        Team team = teamManager.getPlayerTeam(player.getUniqueId());

        if (team == null) {
            player.sendMessage(Component.text("You are not in a team!", NamedTextColor.RED));
            return;
        }

        if (!team.isOwner(player.getUniqueId())) {
            player.sendMessage(Component.text("Only the team owner can invite players!", NamedTextColor.RED));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null || !target.isOnline()) {
            player.sendMessage(Component.text("Player not found!", NamedTextColor.RED));
            return;
        }

        if (teamManager.getPlayerTeam(target.getUniqueId()) != null) {
            player.sendMessage(Component.text("That player is already in a team!", NamedTextColor.RED));
            return;
        }

        if (team.hasInvite(target.getUniqueId())) {
            player.sendMessage(Component.text("That player already has a pending invite!", NamedTextColor.RED));
            return;
        }

        if (teamManager.invitePlayer(team.getId(), target.getUniqueId())) {
            player.sendMessage(Component.text("Successfully invited ", NamedTextColor.GREEN)
                    .append(Component.text(target.getName(), NamedTextColor.GOLD))
                    .append(Component.text(" to your team!", NamedTextColor.GREEN)));

            target.sendMessage(Component.text("You have been invited to join team ", NamedTextColor.YELLOW)
                    .append(Component.text(team.getName(), NamedTextColor.GOLD, TextDecoration.BOLD)));
            target.sendMessage(Component.text("Use ", NamedTextColor.YELLOW)
                    .append(Component.text("/team accept " + team.getName(), NamedTextColor.GREEN))
                    .append(Component.text(" to accept or ", NamedTextColor.YELLOW))
                    .append(Component.text("/team deny " + team.getName(), NamedTextColor.RED))
                    .append(Component.text(" to decline", NamedTextColor.YELLOW)));
        } else {
            player.sendMessage(Component.text("Failed to invite player!", NamedTextColor.RED));
        }
    }

    private void handleKick(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Component.text("Usage: /team kick <player>", NamedTextColor.RED));
            return;
        }

        Team team = teamManager.getPlayerTeam(player.getUniqueId());

        if (team == null) {
            player.sendMessage(Component.text("You are not in a team!", NamedTextColor.RED));
            return;
        }

        if (!team.isOwner(player.getUniqueId())) {
            player.sendMessage(Component.text("Only the team owner can kick players!", NamedTextColor.RED));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage(Component.text("Player not found!", NamedTextColor.RED));
            return;
        }

        if (!team.isMember(target.getUniqueId())) {
            player.sendMessage(Component.text("That player is not in your team!", NamedTextColor.RED));
            return;
        }

        if (team.isOwner(target.getUniqueId())) {
            player.sendMessage(Component.text("You cannot kick yourself!", NamedTextColor.RED));
            return;
        }

        if (teamManager.kickPlayer(team.getId(), target.getUniqueId())) {
            player.sendMessage(Component.text("Successfully kicked ", NamedTextColor.GREEN)
                    .append(Component.text(target.getName(), NamedTextColor.GOLD))
                    .append(Component.text(" from your team!", NamedTextColor.GREEN)));

            if (target.isOnline()) {
                target.sendMessage(Component.text("You have been kicked from team ", NamedTextColor.RED)
                        .append(Component.text(team.getName(), NamedTextColor.GOLD)));
            }
        } else {
            player.sendMessage(Component.text("Failed to kick player!", NamedTextColor.RED));
        }
    }

    private void handleLeave(Player player) {
        Team team = teamManager.getPlayerTeam(player.getUniqueId());

        if (team == null) {
            player.sendMessage(Component.text("You are not in a team!", NamedTextColor.RED));
            return;
        }

        if (team.isOwner(player.getUniqueId())) {
            player.sendMessage(Component.text("You cannot leave your own team! Use /team disband instead.", NamedTextColor.RED));
            return;
        }

        String teamName = team.getName();
        UUID ownerId = team.getOwner();

        if (teamManager.leaveTeam(player.getUniqueId())) {
            player.sendMessage(Component.text("You have left team ", NamedTextColor.YELLOW)
                    .append(Component.text(teamName, NamedTextColor.GOLD)));

            Player owner = Bukkit.getPlayer(ownerId);
            if (owner != null && owner.isOnline()) {
                owner.sendMessage(Component.text(player.getName(), NamedTextColor.GOLD)
                        .append(Component.text(" has left your team!", NamedTextColor.YELLOW)));
            }
        } else {
            player.sendMessage(Component.text("Failed to leave team!", NamedTextColor.RED));
        }
    }

    private void handleAccept(Player player, String[] args) {
        List<Team> invites = teamManager.getTeamInvites(player.getUniqueId());

        if (invites.isEmpty()) {
            player.sendMessage(Component.text("You have no pending team invites!", NamedTextColor.RED));
            return;
        }

        Team team;
        if (args.length >= 2) {
            String teamName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            team = invites.stream()
                    .filter(t -> t.getName().equalsIgnoreCase(teamName))
                    .findFirst()
                    .orElse(null);

            if (team == null) {
                player.sendMessage(Component.text("You have no invite from that team!", NamedTextColor.RED));
                return;
            }
        } else {
            if (invites.size() > 1) {
                player.sendMessage(Component.text("You have multiple pending invites. Please specify: /team accept <team name>", NamedTextColor.YELLOW));
                player.sendMessage(Component.text("Pending invites:", NamedTextColor.GOLD));
                for (Team t : invites) {
                    player.sendMessage(Component.text("  - ", NamedTextColor.GRAY)
                            .append(Component.text(t.getName(), NamedTextColor.GOLD)));
                }
                return;
            }
            team = invites.get(0);
        }

        if (teamManager.acceptInvite(player.getUniqueId(), team.getId())) {
            player.sendMessage(Component.text("You have joined team ", NamedTextColor.GREEN)
                    .append(Component.text(team.getName(), NamedTextColor.GOLD, TextDecoration.BOLD)));

            Player owner = Bukkit.getPlayer(team.getOwner());
            if (owner != null && owner.isOnline()) {
                owner.sendMessage(Component.text(player.getName(), NamedTextColor.GOLD)
                        .append(Component.text(" has joined your team!", NamedTextColor.GREEN)));
            }
        } else {
            player.sendMessage(Component.text("Failed to join team!", NamedTextColor.RED));
        }
    }

    private void handleDeny(Player player, String[] args) {
        List<Team> invites = teamManager.getTeamInvites(player.getUniqueId());

        if (invites.isEmpty()) {
            player.sendMessage(Component.text("You have no pending team invites!", NamedTextColor.RED));
            return;
        }

        Team team;
        if (args.length >= 2) {
            String teamName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            team = invites.stream()
                    .filter(t -> t.getName().equalsIgnoreCase(teamName))
                    .findFirst()
                    .orElse(null);

            if (team == null) {
                player.sendMessage(Component.text("You have no invite from that team!", NamedTextColor.RED));
                return;
            }
        } else {
            if (invites.size() > 1) {
                player.sendMessage(Component.text("You have multiple pending invites. Please specify: /team deny <team name>", NamedTextColor.YELLOW));
                return;
            }
            team = invites.get(0);
        }

        if (teamManager.denyInvite(player.getUniqueId(), team.getId())) {
            player.sendMessage(Component.text("You have declined the invite from ", NamedTextColor.YELLOW)
                    .append(Component.text(team.getName(), NamedTextColor.GOLD)));
        } else {
            player.sendMessage(Component.text("Failed to decline invite!", NamedTextColor.RED));
        }
    }

    private void handleInfo(Player player, String[] args) {
        Team team;

        if (args.length >= 2) {
            String teamName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            team = teamManager.getTeamByName(teamName);

            if (team == null) {
                player.sendMessage(Component.text("Team not found!", NamedTextColor.RED));
                return;
            }
        } else {
            team = teamManager.getPlayerTeam(player.getUniqueId());
            if (team == null) {
                player.sendMessage(Component.text("You are not in a team! Use /team info <team name>", NamedTextColor.RED));
                return;
            }
        }

        player.sendMessage(Component.text("═══════════════════════════════", NamedTextColor.GOLD));
        player.sendMessage(Component.text("Team: ", NamedTextColor.YELLOW)
                .append(Component.text(team.getName(), NamedTextColor.GOLD, TextDecoration.BOLD)));

        Player owner = Bukkit.getPlayer(team.getOwner());
        String ownerName = owner != null ? owner.getName() : "Unknown";
        player.sendMessage(Component.text("Owner: ", NamedTextColor.YELLOW)
                .append(Component.text(ownerName, NamedTextColor.GREEN)));

        player.sendMessage(Component.text("Members (" + team.getMemberCount() + "):", NamedTextColor.YELLOW));
        for (UUID memberId : team.getMembers()) {
            Player member = Bukkit.getPlayer(memberId);
            if (member != null) {
                NamedTextColor color = member.isOnline() ? NamedTextColor.GREEN : NamedTextColor.GRAY;
                player.sendMessage(Component.text("  • ", NamedTextColor.DARK_GRAY)
                        .append(Component.text(member.getName(), color)));
            }
        }

        player.sendMessage(Component.text("═══════════════════════════════", NamedTextColor.GOLD));
    }

    private void handleList(Player player) {
        Collection<Team> teams = teamManager.getAllTeams();

        if (teams.isEmpty()) {
            player.sendMessage(Component.text("No teams exist yet!", NamedTextColor.YELLOW));
            return;
        }

        player.sendMessage(Component.text("═══════════════════════════════", NamedTextColor.GOLD));
        player.sendMessage(Component.text("All Teams (" + teams.size() + "):", NamedTextColor.YELLOW, TextDecoration.BOLD));
        player.sendMessage(Component.text("═══════════════════════════════", NamedTextColor.GOLD));

        for (Team team : teams) {
            Player owner = Bukkit.getPlayer(team.getOwner());
            String ownerName = owner != null ? owner.getName() : "Unknown";

            player.sendMessage(Component.text("• ", NamedTextColor.DARK_GRAY)
                    .append(Component.text(team.getName(), NamedTextColor.GOLD, TextDecoration.BOLD))
                    .append(Component.text(" - ", NamedTextColor.GRAY))
                    .append(Component.text(team.getMemberCount() + " members", NamedTextColor.YELLOW))
                    .append(Component.text(" (Owner: ", NamedTextColor.GRAY))
                    .append(Component.text(ownerName, NamedTextColor.GREEN))
                    .append(Component.text(")", NamedTextColor.GRAY)));
        }

        player.sendMessage(Component.text("═══════════════════════════════", NamedTextColor.GOLD));
    }

    private void sendHelp(Player player) {
        player.sendMessage(Component.text("═══════════════════════════════", NamedTextColor.GOLD));
        player.sendMessage(Component.text("Team Commands", NamedTextColor.YELLOW, TextDecoration.BOLD));
        player.sendMessage(Component.text("═══════════════════════════════", NamedTextColor.GOLD));
        player.sendMessage(Component.text("/team create <name>", NamedTextColor.GREEN)
                .append(Component.text(" - Create a new team", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/team disband", NamedTextColor.GREEN)
                .append(Component.text(" - Disband your team", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/team invite <player>", NamedTextColor.GREEN)
                .append(Component.text(" - Invite a player", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/team kick <player>", NamedTextColor.GREEN)
                .append(Component.text(" - Kick a player", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/team leave", NamedTextColor.GREEN)
                .append(Component.text(" - Leave your team", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/team accept [team]", NamedTextColor.GREEN)
                .append(Component.text(" - Accept an invite", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/team deny [team]", NamedTextColor.GREEN)
                .append(Component.text(" - Deny an invite", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/team info [team]", NamedTextColor.GREEN)
                .append(Component.text(" - View team info", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/team list", NamedTextColor.GREEN)
                .append(Component.text(" - List all teams", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("═══════════════════════════════", NamedTextColor.GOLD));
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            return Arrays.asList("create", "disband", "invite", "kick", "leave", "accept", "deny", "info", "list")
                    .stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            switch (subCommand) {
                case "invite", "kick" -> {
                    return Bukkit.getOnlinePlayers().stream()
                            .map(Player::getName)
                            .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                            .collect(Collectors.toList());
                }
                case "accept", "deny" -> {
                    return teamManager.getTeamInvites(player.getUniqueId()).stream()
                            .map(Team::getName)
                            .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                            .collect(Collectors.toList());
                }
                case "info" -> {
                    return teamManager.getAllTeams().stream()
                            .map(Team::getName)
                            .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                            .collect(Collectors.toList());
                }
            }
        }

        return Collections.emptyList();
    }
}

