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
    private final TeamChatManager chatManager;
    private final TeamHomeManager homeManager;

    public TeamCommand(VoidCore plugin, TeamManager teamManager, TeamChatManager chatManager, TeamHomeManager homeManager) {
        this.plugin = plugin;
        this.teamManager = teamManager;
        this.chatManager = chatManager;
        this.homeManager = homeManager;
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
            case "color", "colour" -> handleColor(player, args);
            case "colors", "colours" -> handleColorList(player);
            case "chat", "c", "tc" -> handleTeamChat(player, args);
            case "socialspy", "spy" -> handleSocialSpy(player);
            case "sethome" -> handleSetHome(player);
            case "home" -> handleHome(player);
            case "delhome", "removehome" -> handleDelHome(player);
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

        net.kyori.adventure.text.format.TextColor teamColor = ColorUtil.hexToTextColor(team.getColor());
        player.sendMessage(Component.text("Team: ", NamedTextColor.YELLOW)
                .append(Component.text(team.getName(), teamColor, TextDecoration.BOLD)));

        player.sendMessage(Component.text("Color: ", NamedTextColor.YELLOW)
                .append(Component.text("■ ", teamColor))
                .append(Component.text(team.getColor(), NamedTextColor.GRAY)));

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
            net.kyori.adventure.text.format.TextColor teamColor = ColorUtil.hexToTextColor(team.getColor());

            player.sendMessage(Component.text("• ", NamedTextColor.DARK_GRAY)
                    .append(Component.text(team.getName(), teamColor, TextDecoration.BOLD))
                    .append(Component.text(" - ", NamedTextColor.GRAY))
                    .append(Component.text(team.getMemberCount() + " members", NamedTextColor.YELLOW))
                    .append(Component.text(" (Owner: ", NamedTextColor.GRAY))
                    .append(Component.text(ownerName, NamedTextColor.GREEN))
                    .append(Component.text(")", NamedTextColor.GRAY)));
        }

        player.sendMessage(Component.text("═══════════════════════════════", NamedTextColor.GOLD));
    }

    private void handleColor(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Component.text("Usage: /team color <color|hex>", NamedTextColor.RED));
            player.sendMessage(Component.text("Examples:", NamedTextColor.YELLOW));
            player.sendMessage(Component.text("  /team color red", NamedTextColor.GRAY));
            player.sendMessage(Component.text("  /team color #FF5555", NamedTextColor.GRAY));
            player.sendMessage(Component.text("Use /team colors to see all available colors", NamedTextColor.GRAY));
            return;
        }

        Team team = teamManager.getPlayerTeam(player.getUniqueId());

        if (team == null) {
            player.sendMessage(Component.text("You are not in a team!", NamedTextColor.RED));
            return;
        }

        if (!team.isOwner(player.getUniqueId())) {
            player.sendMessage(Component.text("Only the team owner can change the team color!", NamedTextColor.RED));
            return;
        }

        String colorInput = args[1];
        String parsedColor = ColorUtil.parseColor(colorInput);

        if (parsedColor == null) {
            player.sendMessage(Component.text("Invalid color! Use /team colors to see available colors.", NamedTextColor.RED));
            player.sendMessage(Component.text("You can use named colors (red, blue, etc.) or hex codes (#FF5555)", NamedTextColor.YELLOW));
            return;
        }

        team.setColor(parsedColor);
        teamManager.saveTeams();

        net.kyori.adventure.text.format.TextColor textColor = ColorUtil.hexToTextColor(parsedColor);

        player.sendMessage(Component.text("Team color updated to: ", NamedTextColor.GREEN)
                .append(Component.text(team.getName(), textColor, TextDecoration.BOLD))
                .append(Component.text(" (" + parsedColor + ")", NamedTextColor.GRAY)));

        // Notify all team members
        for (UUID memberId : team.getMembers()) {
            if (!memberId.equals(player.getUniqueId())) {
                Player member = Bukkit.getPlayer(memberId);
                if (member != null && member.isOnline()) {
                    member.sendMessage(Component.text("Your team color has been changed to: ", NamedTextColor.YELLOW)
                            .append(Component.text(team.getName(), textColor, TextDecoration.BOLD)));
                }
            }
        }
    }

    private void handleColorList(Player player) {
        player.sendMessage(Component.text("═══════════════════════════════", NamedTextColor.GOLD));
        player.sendMessage(Component.text("Available Team Colors", NamedTextColor.YELLOW, TextDecoration.BOLD));
        player.sendMessage(Component.text("═══════════════════════════════", NamedTextColor.GOLD));
        player.sendMessage(Component.text("You can use any of these color names or hex codes:", NamedTextColor.GRAY));
        player.sendMessage(Component.text(""));

        // Display named colors in a nice format
        player.sendMessage(Component.text("● ", ColorUtil.hexToTextColor("#FF5555"))
                .append(Component.text("red ", NamedTextColor.GRAY))
                .append(Component.text("● ", ColorUtil.hexToTextColor("#FFAA00")))
                .append(Component.text("gold/orange ", NamedTextColor.GRAY))
                .append(Component.text("● ", ColorUtil.hexToTextColor("#FFFF55")))
                .append(Component.text("yellow", NamedTextColor.GRAY)));

        player.sendMessage(Component.text("● ", ColorUtil.hexToTextColor("#55FF55"))
                .append(Component.text("green/lime ", NamedTextColor.GRAY))
                .append(Component.text("● ", ColorUtil.hexToTextColor("#00AA00")))
                .append(Component.text("dark_green ", NamedTextColor.GRAY))
                .append(Component.text("● ", ColorUtil.hexToTextColor("#55FFFF")))
                .append(Component.text("aqua/cyan", NamedTextColor.GRAY)));

        player.sendMessage(Component.text("● ", ColorUtil.hexToTextColor("#5555FF"))
                .append(Component.text("blue ", NamedTextColor.GRAY))
                .append(Component.text("● ", ColorUtil.hexToTextColor("#0000AA")))
                .append(Component.text("dark_blue ", NamedTextColor.GRAY))
                .append(Component.text("● ", ColorUtil.hexToTextColor("#00AAAA")))
                .append(Component.text("dark_aqua", NamedTextColor.GRAY)));

        player.sendMessage(Component.text("● ", ColorUtil.hexToTextColor("#FF55FF"))
                .append(Component.text("light_purple/pink ", NamedTextColor.GRAY))
                .append(Component.text("● ", ColorUtil.hexToTextColor("#AA00AA")))
                .append(Component.text("dark_purple/purple", NamedTextColor.GRAY)));

        player.sendMessage(Component.text("● ", ColorUtil.hexToTextColor("#FFFFFF"))
                .append(Component.text("white ", NamedTextColor.GRAY))
                .append(Component.text("● ", ColorUtil.hexToTextColor("#AAAAAA")))
                .append(Component.text("gray ", NamedTextColor.GRAY))
                .append(Component.text("● ", ColorUtil.hexToTextColor("#555555")))
                .append(Component.text("dark_gray", NamedTextColor.GRAY)));

        player.sendMessage(Component.text("● ", ColorUtil.hexToTextColor("#AA0000"))
                .append(Component.text("dark_red ", NamedTextColor.GRAY))
                .append(Component.text("● ", ColorUtil.hexToTextColor("#000000")))
                .append(Component.text("black", NamedTextColor.WHITE)));

        player.sendMessage(Component.text(""));
        player.sendMessage(Component.text("Or use any hex code: ", NamedTextColor.YELLOW)
                .append(Component.text("#FF5555, #00FF00, etc.", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("Example: /team color red", NamedTextColor.GREEN));
        player.sendMessage(Component.text("═══════════════════════════════", NamedTextColor.GOLD));
    }

    private void handleTeamChat(Player player, String[] args) {
        Team team = teamManager.getPlayerTeam(player.getUniqueId());

        if (team == null) {
            player.sendMessage(Component.text("You are not in a team!", NamedTextColor.RED));
            return;
        }

        // If args provided, send a quick message
        if (args.length > 1) {
            String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            chatManager.sendTeamMessage(player, message);
            return;
        }

        // Otherwise toggle team chat mode
        chatManager.toggleTeamChatMode(player.getUniqueId());
        boolean inMode = chatManager.isInTeamChatMode(player.getUniqueId());

        if (inMode) {
            player.sendMessage(Component.text("═══════════════════════════════", NamedTextColor.GOLD));
            player.sendMessage(Component.text("Team Chat Mode: ", NamedTextColor.YELLOW)
                    .append(Component.text("ENABLED", NamedTextColor.GREEN, TextDecoration.BOLD)));
            player.sendMessage(Component.text("All your messages will now be sent to your team!", NamedTextColor.GRAY));
            player.sendMessage(Component.text("Use /team chat again to toggle off", NamedTextColor.GRAY));
            player.sendMessage(Component.text("═══════════════════════════════", NamedTextColor.GOLD));
        } else {
            player.sendMessage(Component.text("Team Chat Mode: ", NamedTextColor.YELLOW)
                    .append(Component.text("DISABLED", NamedTextColor.RED, TextDecoration.BOLD)));
            player.sendMessage(Component.text("Your messages will now be sent to global chat", NamedTextColor.GRAY));
        }
    }

    private void handleSocialSpy(Player player) {
        if (!player.hasPermission("voidcore.team.socialspy")) {
            player.sendMessage(Component.text("You don't have permission to use this command!", NamedTextColor.RED));
            return;
        }

        chatManager.toggleSocialSpy(player.getUniqueId());
        boolean enabled = chatManager.hasSocialSpyEnabled(player.getUniqueId());

        if (enabled) {
            player.sendMessage(Component.text("═══════════════════════════════", NamedTextColor.GOLD));
            player.sendMessage(Component.text("Team Social Spy: ", NamedTextColor.YELLOW)
                    .append(Component.text("ENABLED", NamedTextColor.GREEN, TextDecoration.BOLD)));
            player.sendMessage(Component.text("You can now see all team chat messages!", NamedTextColor.GRAY));
            player.sendMessage(Component.text("═══════════════════════════════", NamedTextColor.GOLD));
        } else {
            player.sendMessage(Component.text("Team Social Spy: ", NamedTextColor.YELLOW)
                    .append(Component.text("DISABLED", NamedTextColor.RED, TextDecoration.BOLD)));
        }
    }

    private void handleSetHome(Player player) {
        Team team = teamManager.getPlayerTeam(player.getUniqueId());

        if (team == null) {
            player.sendMessage(Component.text(getMessage("team-home.messages.not-in-team", "You are not in a team!"), NamedTextColor.RED));
            return;
        }

        if (!team.isOwner(player.getUniqueId())) {
            player.sendMessage(Component.text(getMessage("team-home.messages.not-owner", "Only the team owner can set the team home!"), NamedTextColor.RED));
            return;
        }

        team.setHomeLocation(player.getLocation());
        teamManager.saveTeams();

        player.sendMessage(Component.text(getMessage("team-home.messages.home-set", "Team home set to your current location!"), NamedTextColor.GREEN));
    }

    private void handleDelHome(Player player) {
        Team team = teamManager.getPlayerTeam(player.getUniqueId());

        if (team == null) {
            player.sendMessage(Component.text(getMessage("team-home.messages.not-in-team", "You are not in a team!"), NamedTextColor.RED));
            return;
        }

        if (!team.isOwner(player.getUniqueId())) {
            player.sendMessage(Component.text(getMessage("team-home.messages.not-owner", "Only the team owner can remove the team home!"), NamedTextColor.RED));
            return;
        }

        team.setHomeLocation(null);
        teamManager.saveTeams();

        player.sendMessage(Component.text(getMessage("team-home.messages.home-removed", "Team home has been removed!"), NamedTextColor.YELLOW));
    }

    private void handleHome(Player player) {
        if (!plugin.getConfig().getBoolean("team-home.enabled", true)) {
            player.sendMessage(Component.text("Team homes are currently disabled!", NamedTextColor.RED));
            return;
        }

        Team team = teamManager.getPlayerTeam(player.getUniqueId());

        if (team == null) {
            player.sendMessage(Component.text(getMessage("team-home.messages.not-in-team", "You are not in a team!"), NamedTextColor.RED));
            return;
        }

        if (!team.hasHome()) {
            player.sendMessage(Component.text(getMessage("team-home.messages.no-home", "Your team doesn't have a home set!"), NamedTextColor.RED));
            return;
        }

        // Check cooldown
        if (homeManager.hasCooldown(player)) {
            long remaining = homeManager.getRemainingCooldown(player);
            String message = getMessage("team-home.messages.on-cooldown", "You must wait {time} seconds before using /team home again!")
                    .replace("{time}", String.valueOf(remaining));
            player.sendMessage(Component.text(message, NamedTextColor.RED));
            return;
        }

        int cost = plugin.getConfig().getInt("team-home.teleport-cost", 5);
        int delay = plugin.getConfig().getInt("team-home.teleport-delay", 3);

        // Check EXP levels
        if (cost > 0 && player.getLevel() < cost) {
            String message = getMessage("team-home.messages.not-enough-exp", "You need {cost} levels to teleport! You have {current} levels.")
                    .replace("{cost}", String.valueOf(cost))
                    .replace("{current}", String.valueOf(player.getLevel()));
            player.sendMessage(Component.text(message, NamedTextColor.RED));
            return;
        }

        org.bukkit.Location destination = team.getHomeLocation();

        if (delay > 0) {
            // Send teleporting message
            String delayMessage = getMessage("team-home.messages.teleporting", "Teleporting to team home in {delay} seconds...")
                    .replace("{delay}", String.valueOf(delay));
            player.sendMessage(Component.text(delayMessage, NamedTextColor.YELLOW));

            // Store original location for movement check
            final org.bukkit.Location originalLocation = player.getLocation().clone();
            final boolean cancelOnMove = plugin.getConfig().getBoolean("team-home.cancel-on-move", true);
            final boolean cancelOnDamage = plugin.getConfig().getBoolean("team-home.cancel-on-damage", true);

            // Schedule teleport
            new org.bukkit.scheduler.BukkitRunnable() {
                int countdown = delay;

                @Override
                public void run() {
                    if (!player.isOnline()) {
                        cancel();
                        return;
                    }

                    // Check for movement
                    if (cancelOnMove && player.getLocation().distance(originalLocation) > 0.5) {
                        player.sendMessage(Component.text(getMessage("team-home.messages.teleport-cancelled-move", "Teleport cancelled - you moved!"), NamedTextColor.RED));
                        cancel();
                        return;
                    }

                    countdown--;

                    if (countdown <= 0) {
                        // Perform teleport
                        performTeleport(player, destination, cost);
                        cancel();
                    }
                }
            }.runTaskTimer(plugin, 20L, 20L); // Run every second

            // Register damage listener
            if (cancelOnDamage) {
                new org.bukkit.event.Listener() {
                    @org.bukkit.event.EventHandler
                    public void onDamage(org.bukkit.event.entity.EntityDamageEvent event) {
                        if (event.getEntity().equals(player)) {
                            player.sendMessage(Component.text(getMessage("team-home.messages.teleport-cancelled-damage", "Teleport cancelled - you took damage!"), NamedTextColor.RED));
                            org.bukkit.event.HandlerList.unregisterAll(this);
                        }
                    }
                };
            }
        } else {
            // Instant teleport
            performTeleport(player, destination, cost);
        }
    }

    private void performTeleport(Player player, org.bukkit.Location destination, int cost) {
        // Deduct EXP
        if (cost > 0) {
            player.setLevel(player.getLevel() - cost);
            String costMessage = getMessage("team-home.messages.exp-cost-deducted", "Spent {cost} levels to teleport.")
                    .replace("{cost}", String.valueOf(cost));
            player.sendMessage(Component.text(costMessage, NamedTextColor.YELLOW));
        }

        // Teleport
        player.teleport(destination);
        player.sendMessage(Component.text(getMessage("team-home.messages.teleport-success", "Teleported to team home!"), NamedTextColor.GREEN));

        // Apply cooldown
        homeManager.applyCooldown(player);
    }

    private String getMessage(String path, String defaultMessage) {
        String message = plugin.getConfig().getString(path, defaultMessage);
        return message.replace("&", "§");
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
        player.sendMessage(Component.text("/team color <color>", NamedTextColor.GREEN)
                .append(Component.text(" - Set team color", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/team colors", NamedTextColor.GREEN)
                .append(Component.text(" - View available colors", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/team chat [message]", NamedTextColor.GREEN)
                .append(Component.text(" - Toggle team chat or send message", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/team sethome", NamedTextColor.GREEN)
                .append(Component.text(" - Set team home (Owner)", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/team home", NamedTextColor.GREEN)
                .append(Component.text(" - Teleport to team home", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/team delhome", NamedTextColor.GREEN)
                .append(Component.text(" - Remove team home (Owner)", NamedTextColor.GRAY)));

        if (player.hasPermission("voidcore.team.socialspy")) {
            player.sendMessage(Component.text("/team socialspy", NamedTextColor.AQUA)
                    .append(Component.text(" - Toggle team chat spy (Admin)", NamedTextColor.DARK_GRAY)));
        }

        player.sendMessage(Component.text("═══════════════════════════════", NamedTextColor.GOLD));
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            List<String> commands = new ArrayList<>(Arrays.asList("create", "disband", "invite", "kick", "leave",
                    "accept", "deny", "info", "list", "color", "colors", "chat", "tc", "sethome", "home", "delhome"));

            if (player.hasPermission("voidcore.team.socialspy")) {
                commands.add("socialspy");
                commands.add("spy");
            }

            return commands.stream()
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
                case "color", "colour" -> {
                    return ColorUtil.getNamedColors().keySet().stream()
                            .filter(color -> color.toLowerCase().startsWith(args[1].toLowerCase()))
                            .sorted()
                            .collect(Collectors.toList());
                }
            }
        }

        return Collections.emptyList();
    }
}

