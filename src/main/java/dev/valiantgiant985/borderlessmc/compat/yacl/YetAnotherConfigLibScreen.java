package dev.valiantgiant985.borderlessmc.compat.yacl;

import dev.valiantgiant985.borderlessmc.BorderlessMCConfig;
import dev.valiantgiant985.borderlessmc.util.ModLoader;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

public final class YetAnotherConfigLibScreen {
    private YetAnotherConfigLibScreen() {
    }

    //? if yacl: >0.0.0 {
    public static boolean isSupported() {
        return ModLoader.getInstance().isModLoaded("yet_another_config_lib_v3");
    }

    public static @Nullable Screen create(BorderlessMCConfig config, String modId, Screen parent) {
        if (!isSupported()) {
            return null;
        }
        return YetAnotherConfigLibScreenImpl.create(config, modId, parent);
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
