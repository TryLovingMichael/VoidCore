package dev.voidlessdev.voidCore.team;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TeamChatManager {
    private final Plugin plugin;
    private final TeamManager teamManager;
    private final Set<UUID> teamChatMode; // Players in team chat mode
    private final Set<UUID> socialSpies; // Admins with social spy enabled

    public TeamChatManager(Plugin plugin, TeamManager teamManager) {
        this.plugin = plugin;
        this.teamManager = teamManager;
        this.teamChatMode = new HashSet<>();
        this.socialSpies = new HashSet<>();
    }

    public boolean isInTeamChatMode(UUID playerId) {
        return teamChatMode.contains(playerId);
    }

    public void toggleTeamChatMode(UUID playerId) {
        if (teamChatMode.contains(playerId)) {
            teamChatMode.remove(playerId);
        } else {
            teamChatMode.add(playerId);
        }
    }

    public void enableTeamChatMode(UUID playerId) {
        teamChatMode.add(playerId);
    }

    public void disableTeamChatMode(UUID playerId) {
        teamChatMode.remove(playerId);
    }

    public boolean hasSocialSpyEnabled(UUID playerId) {
        return socialSpies.contains(playerId);
    }

    public void toggleSocialSpy(UUID playerId) {
        if (socialSpies.contains(playerId)) {
            socialSpies.remove(playerId);
        } else {
            socialSpies.add(playerId);
        }
    }

    public void sendTeamMessage(Player sender, String message) {
        Team team = teamManager.getPlayerTeam(sender.getUniqueId());
        if (team == null) {
            sender.sendMessage(plugin.getConfig().getString("team-chat.not-in-team-message",
                    "&cYou are not in a team!"));
            return;
        }

        String format = plugin.getConfig().getString("team-chat.format",
                "&6[TEAM] &r{player}&7: &f{message}");
        String formattedMessage = format
                .replace("{player}", sender.getName())
                .replace("{message}", message)
                .replace("&", "§");

        // Send to all team members
        for (UUID memberId : team.getMembers()) {
            Player member = plugin.getServer().getPlayer(memberId);
            if (member != null && member.isOnline()) {
                member.sendMessage(formattedMessage);
            }
        }

        // Send to social spies (admins)
        String spyFormat = plugin.getConfig().getString("team-chat.spy-format",
                "&8[SPY] &6[{team}] &r{player}&7: &f{message}");
        String spyMessage = spyFormat
                .replace("{team}", team.getName())
                .replace("{player}", sender.getName())
                .replace("{message}", message)
                .replace("&", "§");

        for (UUID spyId : socialSpies) {
            // Don't send to team members (they already got the message)
            if (team.isMember(spyId)) {
                continue;
            }

            Player spy = plugin.getServer().getPlayer(spyId);
            if (spy != null && spy.isOnline()) {
                spy.sendMessage(spyMessage);
            }
        }
    }

    public void removePlayer(UUID playerId) {
        teamChatMode.remove(playerId);
        socialSpies.remove(playerId);
    }
}

