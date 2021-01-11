package me.nahu.portals.portal;

import com.google.common.collect.ImmutableMap;
import me.nahu.portals.api.entities.Portal;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class BasicPortal implements Portal {
    private final String id;

    private World world;

    private Vector maxPoint;
    private Vector minPoint;

    private Location maxLocation;
    private Location minLocation;

    private String command;

    private boolean available;

    public BasicPortal(
            @NotNull String id,
            @NotNull World world,
            @NotNull Vector maxPoint,
            @NotNull Vector minPoint,
            boolean available
    ) {
        this(id, world, maxPoint, minPoint, null, available);
    }

    public BasicPortal(
            @NotNull String id,
            @NotNull World world,
            @NotNull Vector maxPoint,
            @NotNull Vector minPoint,
            @Nullable String command,
            boolean available
    ) {
        this.id = id;
        this.world = world;
        this.maxPoint = maxPoint;
        this.minPoint = minPoint;
        this.command = command;
        this.available = available;
    }

    @Override
    public @NotNull String getId() {
        return id;
    }

    @Override
    public @NotNull World getWorld() {
        return world;
    }

    @Override
    public void changeWorld(@NotNull World world) {
        this.world = world;
    }

    @Override
    public @NotNull Location getMaxLocation() {
        return maxLocation;
    }

    @Override
    public @NotNull Vector getMaxPoint() {
        return maxPoint;
    }

    @Override
    public void setMaxPoint(@NotNull Vector maxPoint) {
        this.maxPoint = maxPoint;
        this.maxLocation = new Location(world, maxPoint.getBlockX(), maxPoint.getBlockY(), maxPoint.getBlockY());
    }

    @Override
    public @NotNull Location getMinLocation() {
        return minLocation;
    }

    @Override
    public @NotNull Vector getMinPoint() {
        return minPoint;
    }

    @Override
    public void setMinPoint(@NotNull Vector minPoint) {
        this.minPoint = minPoint;
        this.minLocation = new Location(world, minPoint.getBlockX(), minPoint.getBlockY(), minPoint.getBlockY());
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    @Override
    public void setAvailability(boolean availability) {
        this.available = availability;
    }

    @Override
    public @NotNull Optional<String> getCommand() {
        return Optional.ofNullable(command);
    }

    @Override
    public void setCommand(@NotNull String command) {
        this.command = command;
    }

    @Override
    public void invokeCommand(@NotNull Player player) {
        getCommand().ifPresent(command -> Bukkit.getServer().dispatchCommand(
            Bukkit.getConsoleSender(),
            command.replace("%player%", player.getName())
        ));
    }

    @Override
    public Map<String, Object> serialize() {
        return ImmutableMap.<String, Object>builder()
                .put("id", id)
                .put("world", world.getUID().toString())
                .put("available", available)
                .put("max", maxPoint)
                .put("min", minPoint)
                .put("command", getCommand().orElse("none"))
                .build();
    }

    @NotNull
    public static BasicPortal deserialize(@NotNull Map<String, Object> args) {
        World world = Bukkit.getWorld(UUID.fromString((String) args.get("world")));
        if (world == null) {
            throw new IllegalArgumentException("unknown world");
        }
        String id = (String) args.get("id");
        String command = (String) args.get("command");
        Vector max = (Vector) args.get("max");
        Vector min = (Vector) args.get("min");
        boolean available = (boolean) args.get("available");
        return new BasicPortal(id, world, max, min, (command != null && command.equals("none")) ? null : command, available);
    }
}
