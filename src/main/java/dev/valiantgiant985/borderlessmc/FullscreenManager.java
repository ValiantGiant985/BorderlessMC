package dev.valiantgiant985.borderlessmc;

import dev.valiantgiant985.borderlessmc.util.MinecraftWindow;

public interface FullscreenManager {
    FullscreenMode getFullscreenMode();

    void setFullscreenMode(FullscreenMode mode);

    void applyFullscreenMode();

    static FullscreenManager getInstance() {
        Object window = MinecraftWindow.getInstance();
        if (window instanceof FullscreenManager manager) {
            return manager;
        }
        throw new IllegalStateException("Minecraft window is missing the BorderlessMC fullscreen controller");
    }
}
