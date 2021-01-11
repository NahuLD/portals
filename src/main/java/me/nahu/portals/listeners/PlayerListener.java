package me.nahu.portals.listeners;

import me.nahu.portals.api.PortalsLibrary;
import me.nahu.portals.api.entities.Portal;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PlayerListener implements Listener {
    private final PortalsLibrary portalsLibrary;

    public PlayerListener(@NotNull PortalsLibrary portalsLibrary) {
        this.portalsLibrary = portalsLibrary;
    }

    @EventHandler
    public void onPlayerMove(@NotNull PlayerMoveEvent event) {
        if (event.getTo() == null) {
            return;
        }

        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
            event.getFrom().getBlockY() == event.getTo().getBlockY() &&
            event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        Player player = event.getPlayer();
        portalsLibrary.getPortalAt(player.getLocation()).ifPresent(portal -> portal.invokeCommand(player));
    }
}
