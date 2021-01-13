package me.nahu.portals.api.entities;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Portal API entity.
 * @version 0.1.0
 * @since 0.1.0
 */
public interface Portal extends ConfigurationSerializable {
    /**
     * Get the ID for this Portal.
     * @return Portal ID.
     */
    @NotNull
    String getName();

    /**
     * Get the world this Portal is located in.
     * @return {@link World} world.
     */
    @NotNull
    World getWorld();

    /**
     * Change the world for this portal.
     * @param world New world.
     */
    void changeWorld(@NotNull World world);

    /**
     * Get the maximum location for this Portal.
     * @return {@link Location} max location.
     */
    @NotNull
    Location getMaxLocation();

    /**
     * Get the maximum point for this Portal.
     * @return {@link Vector} max point.
     */
    @NotNull
    Vector getMaxPoint();

    /**
     * Set a new maximum point for this Portal.
     * @param maxPoint New max point.
     */
    void setMaxPoint(@NotNull Vector maxPoint);

    /**
     * Get the minimum location for this Portal.
     * @return {@link Location} min location.
     */
    @NotNull
    Location getMinLocation();

    /**
     * Get the minimum point for this Portal.
     * @return {@link Vector} min point.
     */
    @NotNull
    Vector getMinPoint();

    /**
     * Set a new minimum point for this Portal.
     * @param minPoint New min point.
     */
    void setMinPoint(@NotNull Vector minPoint);

    /**
     * Check whether this portals' command is sent by the console.
     * @return {@link Boolean} available.
     */
    boolean isConsoleCommand();

    /**
     * Set true if you want this command to be sent by the console.
     * @param consoleCommand New value.
     */
    void setConsoleCommand(boolean consoleCommand);

    /**
     * Get the command that will be run once it's activated.
     * @return {@link String} command.
     */
    @NotNull
    Optional<String> getCommand();

    /**
     * Change the command that is run. Placeholder is %player%, which will replace the player name with.
     * @param command New command.
     */
    void setCommand(@Nullable String command);

    /**
     * Invoke the command above onto a certain player.
     * @param player Player to invoke the command from.
     */
    void invokeCommand(@NotNull Player player);

    /**
     * Check if a player is inside this Portal's boundaries.
     * @param player {@link Player} player to check.
     * @return {@link Boolean} whether it's inside of the portal boundaries.
     */
    default boolean isInside(@NotNull Player player) {
        return isInside(player.getLocation());
    }

    /**
     * Check if a location is inside this Portal's boundaries.
     * @param location {@link Location} location to check.
     * @return {@link Boolean} whether it's inside of the portal boundaries.
     */
    default boolean isInside(@NotNull Location location) {
        return isInside(location.toVector());
    }

    /**
     * Check if a vector is inside this Portal's boundaries.
     * @param vector {@link Vector} vector to check.
     * @return {@link Boolean} whether it's inside of the portal boundaries.
     */
    default boolean isInside(@NotNull Vector vector) {
        return vector.getBlockX() >= getMinPoint().getX() &&
                vector.getBlockX() <= getMaxPoint().getX() &&
                vector.getBlockY() >= getMinPoint().getY() &&
                vector.getBlockY() <= getMaxPoint().getY() &&
                vector.getBlockZ() >= getMinPoint().getZ() &&
                vector.getBlockZ() <= getMaxPoint().getZ();
    }
}
