package dev.valiantgiant985.borderlessmc.util;

import net.minecraft.network.chat.Component;

public final class Components {
    private Components() {
    }

    public static Component translatable(String translationKey) {
        return Component.translatable(translationKey);
    }

    public static Component literal(String text) {
        return Component.literal(text);
    }
}
