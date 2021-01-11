package me.nahu.portals.utils;

import org.jetbrains.annotations.NotNull;

public final class Pair<L, R> {
    private final L left;
    private final R right;

    private Pair(@NotNull L left, @NotNull R right) {
        this.left = left;
        this.right = right;
    }

    @NotNull
    public L getLeft() {
        return left;
    }

    @NotNull
    public R getRight() {
        return right;
    }

    @NotNull
    public static <L, R> Pair<L, R> of(@NotNull L left, @NotNull R right) {
        return new Pair<>(left, right);
    }
}
