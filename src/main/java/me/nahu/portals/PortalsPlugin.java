package me.nahu.portals;

import co.aikar.commands.BukkitCommandManager;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import me.nahu.portals.api.entities.Portal;
import me.nahu.portals.command.PortalCommand;
import me.nahu.portals.listeners.PlayerListener;
import me.nahu.portals.portal.BasicPortal;
import me.tom.sparse.spigot.chat.menu.ChatMenuAPI;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;

public class PortalsPlugin extends JavaPlugin {
    static {
        ConfigurationSerialization.registerClass(BasicPortal.class, "Portal");
    }

    private PortalsManager portalsManager;
    private BukkitCommandManager commandManager;

    private File portalsFile;

    @Override
    public void onEnable() {
        WorldEditPlugin worldEditPlugin = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");
        ChatMenuAPI.init(this);

        saveResource("portals.yml", false);

        portalsFile = new File(getDataFolder(), "portals.yml");
        portalsManager = new PortalsManager(YamlConfiguration.loadConfiguration(portalsFile));

        commandManager = new BukkitCommandManager(this);
        commandManager.getCommandCompletions().registerAsyncCompletion(
                "portals",
                context -> portalsManager.getPortals().stream().map(Portal::getId).collect(Collectors.toSet())
        );

        commandManager.registerCommand(new PortalCommand(portalsManager, worldEditPlugin));
        getServer().getPluginManager().registerEvents(new PlayerListener(portalsManager), this);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        commandManager.unregisterCommands();
        ChatMenuAPI.disable();
        try {
            portalsManager.savePortals(portalsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
