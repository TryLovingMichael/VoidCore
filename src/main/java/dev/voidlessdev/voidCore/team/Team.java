package dev.voidlessdev.voidCore.team;

import java.util.*;

public class Team {
    private final UUID id;
    private String name;
    private UUID owner;
    private final Set<UUID> members;
    private final Set<UUID> pendingInvites;
    private final long createdAt;
    private String color; // Hex color code (e.g., "#FF5555" or legacy codes like "&c")

    public Team(String name, UUID owner) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.owner = owner;
        this.members = new HashSet<>();
        this.members.add(owner);
        this.pendingInvites = new HashSet<>();
        this.createdAt = System.currentTimeMillis();
        this.color = "#FFFFFF"; // Default white
    }

    public Team(UUID id, String name, UUID owner, Set<UUID> members, Set<UUID> pendingInvites, long createdAt, String color) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.members = members;
        this.pendingInvites = pendingInvites;
        this.createdAt = createdAt;
        this.color = color != null ? color : "#FFFFFF";
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public Set<UUID> getMembers() {
        return new HashSet<>(members);
    }

    public boolean addMember(UUID playerId) {
        return members.add(playerId);
    }

    public boolean removeMember(UUID playerId) {
        return members.remove(playerId);
    }

    public boolean isMember(UUID playerId) {
        return members.contains(playerId);
    }

    public boolean isOwner(UUID playerId) {
        return owner.equals(playerId);
    }

    public Set<UUID> getPendingInvites() {
        return new HashSet<>(pendingInvites);
    }

    public void addInvite(UUID playerId) {
        pendingInvites.add(playerId);
    }

    public void removeInvite(UUID playerId) {
        pendingInvites.remove(playerId);
    }

    public boolean hasInvite(UUID playerId) {
        return pendingInvites.contains(playerId);
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public int getMemberCount() {
        return members.size();
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}

