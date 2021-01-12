package me.nahu.portals.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.google.common.collect.Lists;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import de.themoep.minedown.MineDown;
import me.nahu.portals.PortalsManager;
import me.nahu.portals.api.entities.Portal;
import me.nahu.portals.utils.Pair;
import me.tom.sparse.spigot.chat.menu.ChatMenu;
import me.tom.sparse.spigot.chat.menu.element.BooleanElement;
import me.tom.sparse.spigot.chat.menu.element.ButtonElement;
import me.tom.sparse.spigot.chat.menu.element.InputElement;
import me.tom.sparse.spigot.chat.menu.element.TextElement;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@CommandAlias("portals|portal")
public class PortalCommand extends BaseCommand {
    private final PortalsManager portalsManager;
    private final WorldEditPlugin worldEditPlugin;

    private final String noPortalFound;
    private final String noSelections;
    private final String portalCreated;
    private final String portalUpdated;
    private final List<String> portalList;

    public PortalCommand(
            @NotNull PortalsManager portalsManager,
            @NotNull WorldEditPlugin worldEditPlugin,
            @NotNull Configuration configuration
    ) {
        this.portalsManager = portalsManager;
        this.worldEditPlugin = worldEditPlugin;

        this.noPortalFound = configuration.getString("no-portal-found", "N/A");
        this.noSelections = configuration.getString("no-selections", "N/A");
        this.portalCreated = configuration.getString("created-portal", "N/A");
        this.portalUpdated = configuration.getString("updated-portal", "N/A");
        this.portalList = configuration.getStringList("portals-list");
    }

    @Subcommand("create")
    @CommandCompletion("Name!")
    public void create(
            @NotNull Player player,
            @NotNull String name,
            @co.aikar.commands.annotation.Optional Boolean consoleRun, // we have to use this sadly
            @co.aikar.commands.annotation.Optional String[] command
    ) {
        Pair<Vector, Vector> selections = getSelections(player);
        if (selections == null) return;

        Portal portal = portalsManager.createPortal(name, player.getWorld(), selections.getLeft(), selections.getRight());
        if (consoleRun != null) portal.setConsoleCommand(consoleRun);
        if (command != null) portal.setCommand(String.join(" ", command));

        player.spigot().sendMessage(
                MineDown.parse(this.portalCreated, "portal_name", portal.getName())
        );
    }

    @Subcommand("consoleedit|ce")
    @CommandCompletion("@portals true|false")
    public void edit(
            @NotNull CommandSender commandSender,
            @NotNull String id,
            @co.aikar.commands.annotation.Optional Boolean consoleCommand,
            @co.aikar.commands.annotation.Optional @Nullable String[] command
    ) {
        Optional<Portal> found = portalsManager.getPortalById(id);
        if (!found.isPresent()) {
            commandSender.sendMessage(TextComponent.toLegacyText(MineDown.parse(this.noPortalFound)));
            return;
        }
        Portal portal = found.get();
        if (consoleCommand != null) portal.setConsoleCommand(consoleCommand);
        if (command != null) portal.setCommand(String.join(" ", command));
        portalsManager.savePortal(portal);

        commandSender.sendMessage(
                TextComponent.toLegacyText(MineDown.parse(this.portalUpdated, "portal_name", portal.getName()))
        );
    }

    @Subcommand("edit")
    @CommandCompletion("@portals")
    public void edit(@NotNull Player player, @NotNull String id) {
        Optional<Portal> found = portalsManager.getPortalById(id);
        if (!found.isPresent()) {
            player.spigot().sendMessage(MineDown.parse(this.noPortalFound));
            return;
        }
        Portal portal = found.get();
        ChatMenu editMenu = new ChatMenu().pauseChat();

        // HEADER
        editMenu.add(new TextElement(ChatColor.YELLOW + "Editing menu for Portal " + ChatColor.RED + portal.getName(), 5, 8));

        // AVAILABLE TOGGLE
        editMenu.add(new TextElement("Is sent by console? ", 5, 10));
        BooleanElement consoleCommand = editMenu.add(new BooleanElement(5, 11, portal.isConsoleCommand()));

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
            portal.setConsoleCommand(consoleCommand.getValue());
            if (command.getValue() != null && !command.getValue().equals("None")) {
                portal.setCommand(command.getValue());
            }

            player.spigot().sendMessage(MineDown.parse(this.portalUpdated, "portal_name", portal.getName()));
            portalsManager.savePortal(portal);
            editMenu.close(clicker);
        }));
        editMenu.openFor(player);
    }

    @Default
    @Subcommand("list")
    public void list(@NotNull CommandSender sender) {
        List<BaseComponent[]> messages = Lists.newArrayList();
        if (portalList.size() > 1) {
            portalList.subList(0, portalList.size() - 1).stream().map(MineDown::parse).forEach(messages::add);
        }
        String portalMessage = portalList.get(portalList.size() - 1);
        portalsManager.getPortals().stream().map(portal ->
            MineDown.parse(portalMessage,
                "portal_name", portal.getName(),
                "max_point", portal.getMaxPoint().toString(),
                "min_point", portal.getMinPoint().toString()
            )
        ).forEach(messages::add);

        if (sender instanceof Player) messages.forEach(((Player) sender).spigot()::sendMessage);
        else messages.stream().map(TextComponent::toLegacyText).forEach(sender::sendMessage);
    }

    @Nullable
    public Pair<Vector, Vector> getSelections(@NotNull Player player) {
        Selection selection = worldEditPlugin.getSelection(player);
        if (selection == null) {
            player.spigot().sendMessage(MineDown.parse(this.noSelections));
            return null;
        }
        return Pair.of(
            selection.getMaximumPoint().toVector(),
            selection.getMinimumPoint().toVector()
        );
    }
}
