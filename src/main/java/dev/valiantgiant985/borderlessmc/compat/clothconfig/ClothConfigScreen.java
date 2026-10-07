package dev.valiantgiant985.borderlessmc.compat.clothconfig;

import dev.valiantgiant985.borderlessmc.BorderlessMCConfig;
import dev.valiantgiant985.borderlessmc.util.ModLoader;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

public final class ClothConfigScreen {
    private ClothConfigScreen() {
    }

    //? if cloth-config: >0.0.0 {
    public static boolean isSupported() {
        ModLoader loader = ModLoader.getInstance();
        String[] ids = {"cloth-config", "cloth-config2", "cloth_config", "cloth_config2"};
        for (String id : ids) {
            if (loader.isModLoaded(id)) {
                return true;
            }
        }
        return false;
    }

    public static @Nullable Screen create(BorderlessMCConfig config, String modId, Screen parent) {
        if (!isSupported()) {
            return null;
        }
        return ClothConfigScreenImpl.create(config, modId, parent);
    }
    //?} else {
    /*public static boolean isSupported() {
        return false;
    }

    public static @Nullable Screen create(BorderlessMCConfig config, String modId, Screen parent) {
        return null;
    }*/
    //?}
}
