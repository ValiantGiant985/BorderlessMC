//? if sodium: >=0.8.0 {
package dev.valiantgiant985.borderlessmc.compat.sodium;

import dev.valiantgiant985.borderlessmc.BorderlessMC;
import dev.valiantgiant985.borderlessmc.FullscreenManager;
import dev.valiantgiant985.borderlessmc.FullscreenMode;
import dev.valiantgiant985.borderlessmc.util.Components;
import net.caffeinemc.mods.sodium.api.config.*;
import net.caffeinemc.mods.sodium.api.config.option.*;
import net.caffeinemc.mods.sodium.api.config.structure.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

@ConfigEntryPointForge(BorderlessMC.MOD_ID)
public final class SodiumConfigBuilder implements ConfigEntryPoint {
    private static final String[] VANILLA_FULLSCREEN_OPTIONS = {
        "sodium:general.fullscreen",
        "sodium:general.fullscreen_mode"
    };

    @Override
    public void registerConfigLate(ConfigBuilder config) {
        FullscreenManager fullscreen = FullscreenManager.getInstance();
        EnumOptionBuilder<FullscreenMode> selector = config
            .createEnumOption(modIdentifier("general.fullscreen"), FullscreenMode.class)
            .setName(Components.translatable("options.fullscreen"))
            .setTooltip(resolveTooltip())
            .setImpact(OptionImpact.HIGH)
            .setDefaultValue(FullscreenMode.OFF)
            .setElementNameProvider(value -> Components.translatable(value.getTranslationKey()))
            .setBinding(fullscreen::setFullscreenMode, fullscreen::getFullscreenMode)
            .setStorageHandler(BorderlessMC.CONFIG::save);

        ModOptionsBuilder options = config.registerOwnModOptions();
        for (String optionId : VANILLA_FULLSCREEN_OPTIONS) {
            install(options, Identifier.parse(optionId), selector, false);
        }

        OptionBuilder resolution = config
            .createIntegerOption(Identifier.parse("sodium:general.fullscreen_resolution"))
            .setEnabledProvider(ignored -> true);
        install(options, Identifier.parse("sodium:general.fullscreen_resolution"), resolution, true);
    }

    private static Identifier modIdentifier(String path) {
        return Identifier.fromNamespaceAndPath(BorderlessMC.MOD_ID, path);
    }

    private static Component resolveTooltip() {
        String preferredKey = "sodium.options.fullscreen_mode.tooltip";
        Component preferred = Components.translatable(preferredKey);
        return preferredKey.equals(preferred.getString())
            ? Components.translatable("sodium.options.fullscreen.tooltip")
            : preferred;
    }

    private static void install(
        ModOptionsBuilder options,
        Identifier target,
        OptionBuilder option,
        boolean overlay
    ) {
        //? if sodium: >=0.8.13 <0.9.0 || >=0.9.2 {
        try {
            installWithPriority(options, target, option, overlay);
            return;
        } catch (Throwable ignored) {
        }
        //?}

        if (overlay) {
            options.registerOptionOverlay(target, option);
        } else {
            options.registerOptionReplacement(target, option);
        }
    }

    //? if sodium: >=0.8.13 <0.9.0 || >=0.9.2 {
    private static void installWithPriority(
        ModOptionsBuilder options,
        Identifier target,
        OptionBuilder option,
        boolean overlay
    ) {
        if (overlay) {
            options.registerOptionOverlay(target, option, Integer.MAX_VALUE);
        } else {
            options.registerOptionReplacement(target, option, Integer.MAX_VALUE);
        }
    }
    //?}
}
//?}
