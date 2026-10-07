package dev.valiantgiant985.borderlessmc;

import dev.valiantgiant985.borderlessmc.compat.clothconfig.ClothConfigScreen;
import dev.valiantgiant985.borderlessmc.compat.modmenu.NativeConfigScreen;
import dev.valiantgiant985.borderlessmc.compat.yacl.YetAnotherConfigLibScreen;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

public final class BorderlessMC {
    public static final String MOD_ID = "borderlessmc";
    public static final BorderlessMCConfig CONFIG = BorderlessMCConfig.loadById(MOD_ID);

    private BorderlessMC() {
    }

    public static @Nullable Screen createConfigScreen(Screen parent) {
        Screen optionalScreen = YetAnotherConfigLibScreen.create(CONFIG, MOD_ID, parent);
        if (optionalScreen == null) {
            optionalScreen = ClothConfigScreen.create(CONFIG, MOD_ID, parent);
        }
        return optionalScreen != null ? optionalScreen : new NativeConfigScreen(parent);
    }

    private static void provideConfigScreen(Consumer<Function<Screen, Screen>> registrar) {
        registrar.accept(BorderlessMC::createConfigScreen);
    }

    //? if neoforge {
    @net.neoforged.fml.common.Mod(value = MOD_ID, dist = net.neoforged.api.distmarker.Dist.CLIENT)
    public static final class NeoForge {
        public NeoForge(net.neoforged.fml.ModContainer container) {
            provideConfigScreen(factory -> container.registerExtensionPoint(
                net.neoforged.neoforge.client.gui.IConfigScreenFactory.class,
                (_, parent) -> factory.apply(parent)
            ));
        }
    }
    //?}

    //? if forge {
    @net.minecraftforge.fml.common.Mod(MOD_ID)
    @net.minecraftforge.api.distmarker.OnlyIn(net.minecraftforge.api.distmarker.Dist.CLIENT)
    public static final class Forge {
        @SuppressWarnings("removal")
        public Forge() {
            provideConfigScreen(factory ->
                net.minecraftforge.fml.ModLoadingContext.get().registerExtensionPoint(
                    net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory.class,
                    () -> new net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory(
                        (_, parent) -> factory.apply(parent)
                    )
                )
            );
        }
    }
    //?}
}
