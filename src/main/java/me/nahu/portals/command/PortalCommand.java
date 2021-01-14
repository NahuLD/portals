package me.nahu.portals.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.google.common.collect.Lists;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import de.themoep.minedown.MineDown;
import me.nahu.portals.PortalsManager;
import me.nahu.portals.api.entities.Portal;
import me.nahu.portals.chatmenu.ButtonElement;
import me.nahu.portals.utils.Pair;
import me.tom.sparse.spigot.chat.menu.ChatMenu;
import me.tom.sparse.spigot.chat.menu.element.InputElement;
import me.tom.sparse.spigot.chat.menu.element.TextElement;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static co.aikar.commands.ACFBukkitUtil.color;

@CommandAlias("portals|portal")
@CommandPermission("portals.*")
public class PortalCommand extends BaseCommand {
    private static final String BORDER = color("&8&l&m----------------------------------------");

    private final PortalsManager portalsManager;
    private final WorldEditPlugin worldEditPlugin;

    private final String noPortalFound;
    private final String noSelections;
    private final String alreadyExists;
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
        this.alreadyExists = configuration.getString("already-exists", "N/A");
        this.portalCreated = configuration.getString("created-portal", "N/A");
        this.portalUpdated = configuration.getString("updated-portal", "N/A");
        this.portalList = configuration.getStringList("portals-list");
    }

    @Subcommand("create")
    @CommandCompletion("Name! true|false Command!")
    @CommandPermission("portals.create")
    public void create(
            @NotNull Player player,
            @NotNull String name,
            @co.aikar.commands.annotation.Optional @Nullable Boolean consoleRun, // we have to use this sadly
            @co.aikar.commands.annotation.Optional @Nullable String[] command
    ) {
        if (portalsManager.getPortalByName(name).isPresent()) {
            player.spigot().sendMessage(MineDown.parse(this.alreadyExists));
            return;
        }

        Pair<Vector, Vector> selections = getSelections(player);
        if (selections == null) return;

        Portal portal = portalsManager.createPortal(name, player.getWorld(), selections.getLeft(), selections.getRight());
        if (consoleRun != null) portal.setConsoleCommand(consoleRun);
        if (command != null) portal.setCommand(String.join(" ", command));

        player.spigot().sendMessage(
                MineDown.parse(this.portalCreated, "portal_name", portal.getName())
        );
    }

    @Subcommand("edit")
    @CommandCompletion("@portals true|false Command!")
    @CommandPermission("portals.edit")
    public void edit(
            @NotNull CommandSender commandSender,
            @NotNull String name,
            @co.aikar.commands.annotation.Optional @Nullable Boolean consoleCommand,
            @co.aikar.commands.annotation.Optional @Nullable String[] command
    ) {
        Optional<Portal> found = portalsManager.getPortalByName(name);
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

    @SuppressWarnings("ConstantConditions")
    @Subcommand("info|menu")
    @CommandCompletion("@portals")
    @CommandPermission("portals.info")
    public void edit(@NotNull Player player, @NotNull String id) {
        Optional<Portal> found = portalsManager.getPortalByName(id);
        if (!found.isPresent()) {
            player.spigot().sendMessage(MineDown.parse(this.noPortalFound));
            return;
        }
        Portal portal = found.get();

        AtomicBoolean consoleSent = new AtomicBoolean(portal.isConsoleCommand());
        AtomicReference<Pair<Vector, Vector>> selections = new AtomicReference<>(Pair.of(portal.getMaxPoint(), portal.getMinPoint()));

        ChatMenu editMenu = new ChatMenu().pauseChat();

        editMenu.add(new TextElement(BORDER, 0, 11));
        editMenu.add(new TextElement(color("&5" + portal.getName() + " Portal"), 5, 12));

        ButtonElement consoleSentButton = new ButtonElement(
            5,
            14,
            color("&dConsole&7: " + ((consoleSent.get()) ? "&aTrue" : "&cFalse") + " &8[&bUpdate&8]"),
            (clicker, button) -> {
                consoleSent.set(!consoleSent.get());
                button.setText(color("&dConsole&7: " + ((consoleSent.get()) ? "&aTrue" : "&cFalse") + " &8[&bUpdate&8]"));
            }
        );

        editMenu.add(consoleSentButton);

        ButtonElement regionButton = new ButtonElement(
            5,
            15,
            color("&dRegion&7: &8(&a" + selections.get().getLeft().toString() + "&8 - &a" + selections.get().getRight().toString() + "&8) [&bUpdate&8]"),
            (clicker, button) -> {
                Pair<Vector, Vector> selection = getSelections(clicker);
                if (selection != null) selections.set(selection);
                button.setText(
                    color("&dRegion&7: &8(&a" + selections.get().getLeft().toString() + "&8 - &a" + selections.get().getRight().toString() + "&8) [&bUpdate&8]")
                );
            }
        );
        editMenu.add(regionButton);

        editMenu.add(new TextElement(color("&dCommand&7:"), 5, 16));
        InputElement command = editMenu.add(new InputElement(50, 16, 200, portal.getCommand().orElse(null)));

        ButtonElement saveChangesButton = new ButtonElement(
            5,
            18,
            color("&8[&eSave Changes&8]"),
            (clicker, button) -> {
                portal.setConsoleCommand(consoleSent.get());
                portal.setMaxPoint(selections.get().getLeft());
                portal.setMinPoint(selections.get().getRight());
                portal.setCommand(command.getValue());
                portalsManager.savePortal(portal);
                player.spigot().sendMessage(MineDown.parse(this.portalUpdated, "portal_name", portal.getName()));
                editMenu.close(clicker);
            }
        );
        editMenu.add(saveChangesButton);
        editMenu.add(new TextElement(BORDER, 0,19));

        editMenu.openFor(player);
    }

    @Default
    @Subcommand("list")
    @CommandPermission("portals.list")
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
