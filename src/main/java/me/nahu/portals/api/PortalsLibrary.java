package me.nahu.portals.api;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection;
import me.nahu.portals.api.entities.Portal;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Simple and quick Portal API library.
 * @version 0.1.0
 * @since 0.1.0
 */
public interface PortalsLibrary {
    /**
     * Get the portal at a given location.
     * @param location Location you want to get a portal entity at.
     * @return Optional holding the portal, empty if there are none.
     */
    @NotNull
    Optional<Portal> getPortalAt(@NotNull Location location);

    /**
     * Check if a location is within any portal.
     * @param location Location you want to check.
     * @return Whether this location is within any portal.
     */
    boolean isInPortal(@NotNull Location location);

    /**
     * Get a portal by it's id. You may use this Portal entity to update values.
     * @param id Id to look for.
     * @return Optional with the portal, empty if none are found.
     */
    Optional<Portal> getPortalById(@NotNull String id);

    /**
     * Get all portals.
     * @return Immutable collection holding all portals.
     */
    ImmutableCollection<Portal> getPortals();

    /**
     * Create a new portal.
     * @param name Name of the portal.
     * @param world World this portal is contained in.
     * @param maxPoint Maximum point.
     * @param minPoint Minimum point.
     * @return New Portal with a randomly generated id.
     */
    @NotNull
    Portal createPortal(@NotNull String name, @NotNull World world, @NotNull Vector maxPoint, @NotNull Vector minPoint);

    /**
     * Create a new portal based off two locations. Both must be contained in the same world.
     * @param name Name of the portal.
     * @param maxLocation Maximum location point.
     * @param minLocation Minimum location point.
     * @return New portal with a randomly generated id.
     */
    @NotNull
    default Portal createPortal(@NotNull String name, @NotNull Location maxLocation, @NotNull Location minLocation) {
        Preconditions.checkArgument(maxLocation.getWorld().equals(minLocation.getWorld()), "both locations must be on the same world");
        return createPortal(name, maxLocation.getWorld(), maxLocation.toVector(), minLocation.toVector());
    }
}
