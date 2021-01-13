package me.nahu.portals.listeners;

import me.nahu.portals.api.PortalsLibrary;
import me.nahu.portals.utils.Utilities;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerListener implements Listener {
    private final PortalsLibrary portalsLibrary;
    private final int portalDelay;

    private final Set<UUID> cooldown = new HashSet<>();

    public PlayerListener(@NotNull PortalsLibrary portalsLibrary, int portalDelay) {
        this.portalsLibrary = portalsLibrary;
        this.portalDelay = portalDelay;
    }

    @EventHandler
    public void onPlayerMove(@NotNull PlayerMoveEvent event) {
        if (event.getTo() == null) {
            return;
        }

        Player player = event.getPlayer();

        UUID uniqueId = player.getUniqueId();
        if (cooldown.contains(uniqueId)) return;

        portalsLibrary.getPortalAt(player.getLocation()).ifPresent(portal -> {
            portal.invokeCommand(player);
            cooldown.add(uniqueId);
            Utilities.runDelayedTask(() -> cooldown.remove(uniqueId), portalDelay * 20);
        });
    }
}
