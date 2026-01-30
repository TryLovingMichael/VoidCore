package dev.voidlessdev.voidCore.team;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class TeamManager {
    private final Plugin plugin;
    private final Map<UUID, Team> teams; // Team ID -> Team
    private final Map<UUID, UUID> playerTeams; // Player UUID -> Team ID
    private final File teamsFile;

    public TeamManager(Plugin plugin) {
        this.plugin = plugin;
        this.teams = new HashMap<>();
        this.playerTeams = new HashMap<>();
        this.teamsFile = new File(plugin.getDataFolder(), "teams.yml");
        loadTeams();
    }

    public Team createTeam(String name, UUID owner) {
        if (playerTeams.containsKey(owner)) {
            return null; // Player already in a team
        }

        Team team = new Team(name, owner);
        teams.put(team.getId(), team);
        playerTeams.put(owner, team.getId());
        saveTeams();
        return team;
    }

    public boolean disbandTeam(UUID teamId) {
        Team team = teams.get(teamId);
        if (team == null) {
            return false;
        }

        // Remove all players from the team
        for (UUID member : team.getMembers()) {
            playerTeams.remove(member);
        }

        teams.remove(teamId);
        saveTeams();
        return true;
    }

    public boolean invitePlayer(UUID teamId, UUID playerId) {
        Team team = teams.get(teamId);
        if (team == null || playerTeams.containsKey(playerId)) {
            return false;
        }

        team.addInvite(playerId);
        saveTeams();
        return true;
    }

    public boolean acceptInvite(UUID playerId, UUID teamId) {
        Team team = teams.get(teamId);
        if (team == null || !team.hasInvite(playerId) || playerTeams.containsKey(playerId)) {
            return false;
        }

        team.removeInvite(playerId);
        team.addMember(playerId);
        playerTeams.put(playerId, teamId);
        saveTeams();
        return true;
    }

    public boolean denyInvite(UUID playerId, UUID teamId) {
        Team team = teams.get(teamId);
        if (team == null || !team.hasInvite(playerId)) {
            return false;
        }

        team.removeInvite(playerId);
        saveTeams();
        return true;
    }

    public boolean kickPlayer(UUID teamId, UUID playerId) {
        Team team = teams.get(teamId);
        if (team == null || !team.isMember(playerId) || team.isOwner(playerId)) {
            return false;
        }

        team.removeMember(playerId);
        playerTeams.remove(playerId);
        saveTeams();
        return true;
    }

    public boolean leaveTeam(UUID playerId) {
        UUID teamId = playerTeams.get(playerId);
        if (teamId == null) {
            return false;
        }

        Team team = teams.get(teamId);
        if (team == null) {
            return false;
        }

        // Owner cannot leave, must disband or transfer ownership
        if (team.isOwner(playerId)) {
            return false;
        }

        team.removeMember(playerId);
        playerTeams.remove(playerId);
        saveTeams();
        return true;
    }

    public Team getPlayerTeam(UUID playerId) {
        UUID teamId = playerTeams.get(playerId);
        return teamId != null ? teams.get(teamId) : null;
    }

    public Team getTeam(UUID teamId) {
        return teams.get(teamId);
    }

    public Team getTeamByName(String name) {
        return teams.values().stream()
                .filter(team -> team.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public Collection<Team> getAllTeams() {
        return teams.values();
    }

    public List<Team> getTeamInvites(UUID playerId) {
        return teams.values().stream()
                .filter(team -> team.hasInvite(playerId))
                .collect(Collectors.toList());
    }

    public boolean isTeamNameTaken(String name) {
        return teams.values().stream()
                .anyMatch(team -> team.getName().equalsIgnoreCase(name));
    }

    private void loadTeams() {
        if (!teamsFile.exists()) {
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(teamsFile);

        for (String key : config.getKeys(false)) {
            try {
                UUID id = UUID.fromString(key);
                String name = config.getString(key + ".name");
                UUID owner = UUID.fromString(config.getString(key + ".owner"));
                long createdAt = config.getLong(key + ".createdAt");
                String color = config.getString(key + ".color", "#FFFFFF");

                Set<UUID> members = config.getStringList(key + ".members").stream()
                        .map(UUID::fromString)
                        .collect(Collectors.toSet());

                Set<UUID> pendingInvites = config.getStringList(key + ".pendingInvites").stream()
                        .map(UUID::fromString)
                        .collect(Collectors.toSet());

                // Load home location if it exists
                org.bukkit.Location homeLocation = null;
                if (config.contains(key + ".home")) {
                    String worldName = config.getString(key + ".home.world");
                    double x = config.getDouble(key + ".home.x");
                    double y = config.getDouble(key + ".home.y");
                    double z = config.getDouble(key + ".home.z");
                    float yaw = (float) config.getDouble(key + ".home.yaw");
                    float pitch = (float) config.getDouble(key + ".home.pitch");

                    org.bukkit.World world = plugin.getServer().getWorld(worldName);
                    if (world != null) {
                        homeLocation = new org.bukkit.Location(world, x, y, z, yaw, pitch);
                    }
                }

                Team team = new Team(id, name, owner, members, pendingInvites, createdAt, color, homeLocation);
                teams.put(id, team);

                for (UUID member : members) {
                    playerTeams.put(member, id);
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load team: " + key);
                e.printStackTrace();
            }
        }

        plugin.getLogger().info("Loaded " + teams.size() + " teams");
    }

    public void saveTeams() {
        FileConfiguration config = new YamlConfiguration();

        for (Team team : teams.values()) {
            String key = team.getId().toString();
            config.set(key + ".name", team.getName());
            config.set(key + ".owner", team.getOwner().toString());
            config.set(key + ".createdAt", team.getCreatedAt());
            config.set(key + ".color", team.getColor());
            config.set(key + ".members", team.getMembers().stream()
                    .map(UUID::toString)
                    .collect(Collectors.toList()));
            config.set(key + ".pendingInvites", team.getPendingInvites().stream()
                    .map(UUID::toString)
                    .collect(Collectors.toList()));

            // Save home location if it exists
            if (team.hasHome()) {
                org.bukkit.Location home = team.getHomeLocation();
                config.set(key + ".home.world", home.getWorld().getName());
                config.set(key + ".home.x", home.getX());
                config.set(key + ".home.y", home.getY());
                config.set(key + ".home.z", home.getZ());
                config.set(key + ".home.yaw", home.getYaw());
                config.set(key + ".home.pitch", home.getPitch());
            }
        }

        try {
            if (!teamsFile.exists()) {
                teamsFile.getParentFile().mkdirs();
                teamsFile.createNewFile();
            }
            config.save(teamsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save teams!");
            e.printStackTrace();
        }
    }
}

