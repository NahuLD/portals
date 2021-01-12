package me.nahu.portals.utils;

import me.nahu.portals.PortalsPlugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Utilities {
    private static final Plugin PLUGIN = PortalsPlugin.getPlugin(PortalsPlugin.class);

    public static void runTaskAsynchronously(Runnable runnable) {
        new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskAsynchronously(PLUGIN);
    }
}
