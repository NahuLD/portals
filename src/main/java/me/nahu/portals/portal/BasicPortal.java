package me.nahu.portals.portal;

import com.google.common.collect.ImmutableMap;
import me.nahu.portals.api.entities.Portal;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class BasicPortal implements Portal {
    private final String name;

    private World world;

    private Vector maxPoint;
    private Vector minPoint;

    private Location maxLocation;
    private Location minLocation;

    private String command;

    private boolean consoleCommand;

    public BasicPortal(
            @NotNull String name,
            @NotNull World world,
            @NotNull Vector maxPoint,
            @NotNull Vector minPoint,
            boolean consoleCommand
    ) {
        this(name, world, maxPoint, minPoint, null, consoleCommand);
    }

    public BasicPortal(
            @NotNull String name,
            @NotNull World world,
            @NotNull Vector maxPoint,
            @NotNull Vector minPoint,
            @Nullable String command,
            boolean consoleCommand
    ) {
        this.name = name;
        this.world = world;
        this.maxPoint = maxPoint;
        this.minPoint = minPoint;
        this.command = command;
        this.consoleCommand = consoleCommand;
    }

    @Override
    public @NotNull String getName() {
        return name;
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
    public boolean isConsoleCommand() {
        return consoleCommand;
    }

    @Override
    public void setConsoleCommand(boolean consoleCommand) {
        this.consoleCommand = consoleCommand;
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
            consoleCommand ? Bukkit.getConsoleSender() : player,
            command.replace("%player%", player.getName())
        ));
    }

    @Override
    public Map<String, Object> serialize() {
        return ImmutableMap.<String, Object>builder()
                .put("name", name)
                .put("world", world.getUID().toString())
                .put("console-sender", consoleCommand)
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
        boolean consolesender = (boolean) args.get("console-sender");
        return new BasicPortal(id, world, max, min, (command != null && command.equals("none")) ? null : command, consolesender);
    }
}
