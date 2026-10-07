package dev.valiantgiant985.borderlessmc;

import com.mojang.blaze3d.platform.Monitor;
import com.mojang.blaze3d.platform.VideoMode;
import com.mojang.blaze3d.platform.Window;

public interface FullscreenType {
    String getId();

    void enable(Window window, Monitor monitor, VideoMode videoMode);

    void disable(Window window);

    default boolean isSupported() {
        return true;
    }
}
