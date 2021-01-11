package me.nahu.portals.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import me.nahu.portals.api.PortalsLibrary;
import me.nahu.portals.api.entities.Portal;
import me.nahu.portals.utils.Pair;
import me.tom.sparse.spigot.chat.menu.ChatMenu;
import me.tom.sparse.spigot.chat.menu.element.BooleanElement;
import me.tom.sparse.spigot.chat.menu.element.ButtonElement;
import me.tom.sparse.spigot.chat.menu.element.InputElement;
import me.tom.sparse.spigot.chat.menu.element.TextElement;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@CommandAlias("portals|portal")
public class PortalCommand extends BaseCommand {
    private static final HoverEvent HOVER_EDIT_TEXT = new HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            new ComponentBuilder("Click to edit this portal!").create()
    );

    private final PortalsLibrary portalsLibrary;
    private final WorldEditPlugin worldEditPlugin;

    public PortalCommand(@NotNull PortalsLibrary portalsLibrary, @NotNull WorldEditPlugin worldEditPlugin) {
        this.portalsLibrary = portalsLibrary;
        this.worldEditPlugin = worldEditPlugin;
    }

    @Subcommand("create")
    public void create(@NotNull Player player) {
        Pair<Vector, Vector> selections = getSelections(player);
        if (selections == null) return;

        Portal portal = portalsLibrary.createPortal(player.getWorld(), selections.getLeft(), selections.getRight());
        player.spigot().sendMessage(
            new ComponentBuilder("Successfully created portal with the id: ")
                .color(ChatColor.GREEN)
                .append(portal.getId())
                .color(ChatColor.YELLOW)
                .create()
        );
    }

    @Subcommand("consoleedit|ce")
    @CommandCompletion("@portals true|false")
    public void edit(
            @NotNull CommandSender commandSender,
            @NotNull String id,
            boolean availablility,
            @Split(" ") String[] command
    ) {
        Optional<Portal> found = portalsLibrary.getPortalById(id);
        if (!found.isPresent()) {
            commandSender.sendMessage(ChatColor.RED + "No portal found with that ID!");
            return;
        }
        Portal portal = found.get();
        portal.setAvailability(availablility);
        if (command != null) portal.setCommand(String.join(" ", command));
        commandSender.sendMessage(ChatColor.GREEN + "Successfully updated portal: " + ChatColor.YELLOW + portal.getId());
    }

    @Subcommand("edit")
    @CommandCompletion("@portals")
    public void edit(@NotNull Player player, @NotNull String id) {
        Optional<Portal> found = portalsLibrary.getPortalById(id);
        if (!found.isPresent()) {
            player.spigot().sendMessage(
                new ComponentBuilder("No portal found with that ID!")
                    .color(ChatColor.RED)
                    .create()
            );
            return;
        }
        Portal portal = found.get();
        ChatMenu editMenu = new ChatMenu().pauseChat();

        // HEADER
        editMenu.add(new TextElement(ChatColor.YELLOW + "Editing menu for Portal " + ChatColor.RED + portal.getId(), 5, 8));

        // AVAILABLE TOGGLE
        editMenu.add(new TextElement("Available: ", 5, 10));
        BooleanElement available = editMenu.add(new BooleanElement(5, 11, portal.isAvailable()));

        // BOUNDARIES UPDATE
        editMenu.add(new TextElement(
                "Boundaries: " + ChatColor.YELLOW + "(" + portal.getMaxPoint().toString() + " - " + portal.getMinPoint().toString() + ")",
                5,
                13
        ));
        editMenu.add(new ButtonElement(5, 14, ChatColor.AQUA + "Update to latest Selection", clicker -> {
            Pair<Vector, Vector> selections = getSelections(player);
            if (selections == null) return;
            portal.setMaxPoint(selections.getLeft()); // max
            portal.setMinPoint(selections.getRight()); // min
        }));

        // COMMAND UPDATE
        editMenu.add(new TextElement("Command: ", 5, 16));
        InputElement command = editMenu.add(new InputElement(5, 17, 250, portal.getCommand().orElse("None")));

        editMenu.add(new ButtonElement(5, 19, ChatColor.GREEN + "[Update Portal]", clicker -> {
            portal.setAvailability(available.getValue());
            if (command.getValue() != null && !command.getValue().equals("None")) {
                portal.setCommand(command.getValue());
            }

            player.spigot().sendMessage(
                new ComponentBuilder("Updated portal ")
                    .color(ChatColor.GREEN)
                    .append(portal.getId())
                    .color(ChatColor.YELLOW)
                    .append("!")
                    .color(ChatColor.GREEN)
                    .create()
            );

            editMenu.close(clicker);
        }));
        editMenu.openFor(player);
    }

    @Default
    @Subcommand("list")
    public void list(@NotNull Player player) {
        ComponentBuilder builder = new ComponentBuilder("All Portals List").color(ChatColor.YELLOW).append("\n");
        portalsLibrary.getPortals().forEach(portal -> builder.append("- Portal ").color(ChatColor.GRAY)
                .event(HOVER_EDIT_TEXT)
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/portals edit " + portal.getId()))
                .append(portal.getId()).color(ChatColor.BLUE)
                .append(" (").color(ChatColor.DARK_GRAY)
                .append(portal.getMaxPoint().toString()).color(ChatColor.AQUA)
                .append(" - ").color(ChatColor.DARK_GRAY)
                .append(portal.getMinPoint().toString()).color(ChatColor.DARK_AQUA)
                .append(")").color(ChatColor.DARK_GRAY)
                .append("\n", ComponentBuilder.FormatRetention.NONE));
        player.spigot().sendMessage(builder.create());
    }

    @Nullable
    public Pair<Vector, Vector> getSelections(@NotNull Player player) {
        Selection selection = worldEditPlugin.getSelection(player);
        if (selection == null) {
            player.spigot().sendMessage(
                new ComponentBuilder("No selection available!")
                    .color(ChatColor.RED)
                    .create()
            );
            return null;
        }
        return Pair.of(
            selection.getMaximumPoint().toVector(),
            selection.getMinimumPoint().toVector()
        );
    }
}
