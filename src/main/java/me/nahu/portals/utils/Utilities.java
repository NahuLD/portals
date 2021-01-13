package me.nahu.portals.utils;

import me.nahu.portals.PortalsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class Utilities {
    private static final Plugin PLUGIN = PortalsPlugin.getPlugin(PortalsPlugin.class);

    public static String color(String input) {
        return ChatColor.translateAlternateColorCodes('&', input); // im sorry :(
    }

    public static void runDelayedTask(@NotNull Runnable runnable, int delayInTicks) {
        new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskLater(PLUGIN, delayInTicks);
    }

    public static void runTaskAsynchronously(@NotNull Runnable runnable) {
        new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskAsynchronously(PLUGIN);
    }
}
