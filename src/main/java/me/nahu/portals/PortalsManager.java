package me.nahu.portals;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import me.nahu.portals.api.PortalsLibrary;
import me.nahu.portals.api.entities.Portal;
import me.nahu.portals.portal.BasicPortal;
import me.nahu.portals.utils.Utilities;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public final class PortalsManager implements PortalsLibrary {
    private final Map<String, Portal> portals;

    private final YamlConfiguration configuration;
    private final File configurationFile;

    public PortalsManager(@NotNull YamlConfiguration configuration, @NotNull File configurationFile) {
        this.configurationFile = configurationFile;
        this.configuration = configuration;
        this.portals = loadPortals();
    }

    @Override
    public @NotNull Optional<Portal> getPortalAt(@NotNull Location location) {
        return portals.values().stream()
                .filter(portal -> portal.isInside(location))
                .filter(portal -> location.getWorld().equals(portal.getWorld()))
                .findFirst();
    }

    @Override
    public boolean isInPortal(@NotNull Location location) {
        return getPortalAt(location).isPresent();
    }

    @Override
    public Optional<Portal> getPortalById(@NotNull String id) {
        return Optional.ofNullable(portals.get(id));
    }

    @Override
    public ImmutableCollection<Portal> getPortals() {
        return ImmutableSet.copyOf(portals.values());
    }

    @Override
    public @NotNull Portal createPortal(@NotNull String name, @NotNull World world, @NotNull Vector maxPoint, @NotNull Vector minPoint) {
        Portal portal = new BasicPortal(name, world, maxPoint, minPoint, false);
        portals.put(portal.getName(), portal);
        return portal;
    }

    public void savePortals() throws IOException {
        getPortals().forEach(portal -> configuration.set(portal.getName(), portal));
        configuration.save(this.configurationFile);
    }

    public void savePortal(@NotNull Portal portal) {
        configuration.set(portal.getName(), portal);
        Utilities.runTaskAsynchronously(() -> {
            try {
                configuration.save(configurationFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @NotNull
    public Map<String, Portal> loadPortals() {
        Map<String, Portal> builder = Maps.newHashMap();
        configuration.getKeys(false).forEach(ids -> {
            BasicPortal portal = (BasicPortal) configuration.get(ids);
            builder.put(ids, portal);
        });
        return builder;
    }
}
