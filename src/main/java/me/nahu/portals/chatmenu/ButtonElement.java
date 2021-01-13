package me.nahu.portals.chatmenu;

import me.tom.sparse.spigot.chat.menu.IElementContainer;
import me.tom.sparse.spigot.chat.util.Text;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class ButtonElement extends me.tom.sparse.spigot.chat.menu.element.ButtonElement {
    private BiConsumer<Player, ButtonElement> callback;

    public ButtonElement(int x, int y, @NotNull String text) {
        super(x, y, text);
    }

    public ButtonElement(int x, int y, @NotNull String text, @Nullable Consumer<Player> callback) {
        super(x, y, text, callback);
    }

    public ButtonElement(int x, int y, @NotNull String text, @Nullable Function<Player, Boolean> callback) {
        super(x, y, text, callback);
    }

    public ButtonElement(int x, int y, @NotNull String text, @Nullable BiConsumer<Player, ButtonElement> callback) {
        super(x, y, text, player -> true);
        this.callback = callback;
    }

    @Override
    public boolean onClick(@NotNull IElementContainer container, @NotNull Player player) {
        this.callback.accept(player, this);
        return super.onClick(container, player);
    }

    @Override
    public @NotNull List<Text> render(@NotNull IElementContainer context) {
        return super.render(context);
    }

    @Override
    public void edit(@NotNull IElementContainer container, @NotNull String[] args) {
        super.edit(container, args);
    }
}
