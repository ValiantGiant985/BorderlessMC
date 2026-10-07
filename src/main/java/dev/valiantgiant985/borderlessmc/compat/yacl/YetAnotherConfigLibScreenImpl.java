//? if yacl: >0.0.0 {
package dev.valiantgiant985.borderlessmc.compat.yacl;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionFlag;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.impl.controller.DropdownStringControllerBuilderImpl;
import dev.isxander.yacl3.impl.controller.TickBoxControllerBuilderImpl;
import dev.valiantgiant985.borderlessmc.BorderlessMCConfig;
import dev.valiantgiant985.borderlessmc.FullscreenType;
import dev.valiantgiant985.borderlessmc.FullscreenTypes;
import dev.valiantgiant985.borderlessmc.util.Components;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

final class YetAnotherConfigLibScreenImpl {
    private YetAnotherConfigLibScreenImpl() {
    }

    static Screen create(BorderlessMCConfig config, String modId, Screen parent) {
        Minecraft client = Minecraft.getInstance();
        ConfigCategory.Builder category = ConfigCategory.createBuilder()
            .name(Components.translatable("modmenu.nameTranslation." + modId));

        category.option(modeOption(
            modId + ".options.fullscreenType",
            FullscreenTypes.exclusive(),
            config.getFullscreenType(),
            config::setFullscreenType
        ));
        category.option(modeOption(
            modId + ".options.borderlessFullscreenType",
            FullscreenTypes.borderless(),
            config.getBorderlessFullscreenType(),
            config::setBorderlessFullscreenType
        ));
        category.option(toggle(modId + ".options.useDelayedFullscreen", true,
            config::getUseDelayedFullscreen, config::setUseDelayedFullscreen, true));
        category.option(toggle(modId + ".options.useScaledFramebuffer", true,
            config::getUseScaledFramebuffer, config::setUseScaledFramebuffer, true));
        category.option(toggle(modId + ".options.pauseOnLostFocus", false,
            () -> client.options.pauseOnLostFocus, value -> client.options.pauseOnLostFocus = value, false));
        category.option(toggle(modId + ".options.pauseOnLostFocusDuringMultiplayer", true,
            config::getPauseOnLostFocusDuringMultiplayer, config::setPauseOnLostFocusDuringMultiplayer, false));

        return YetAnotherConfigLib.createBuilder()
            .title(Components.translatable("modmenu.nameTranslation." + modId))
            .category(category.build())
            .save(() -> {
                config.save();
                client.options.save();
            })
            .build()
            .generateScreen(parent);
    }

    private static Option<String> modeOption(
        String nameKey,
        FullscreenType defaultMode,
        FullscreenType currentMode,
        java.util.function.Consumer<FullscreenType> setter
    ) {
        String[] values = FullscreenTypes.stream().map(FullscreenType::getId).toArray(String[]::new);
        return Option.<String>createBuilder()
            .name(Components.translatable(nameKey))
            .description(id -> modeDescription(nameKey, id))
            .binding(defaultMode.getId(), currentMode::getId,
                id -> FullscreenTypes.get(id).ifPresent(setter))
            .controller(option -> new DropdownStringControllerBuilderImpl(option).values(values))
            .flag(OptionFlag.GAME_RESTART)
            .build();
    }

    private static OptionDescription modeDescription(String nameKey, String modeId) {
        String optionId = nameKey.substring(nameKey.lastIndexOf('.') + 1);
        String modId = nameKey.substring(0, nameKey.indexOf('.'));
        return OptionDescription.createBuilder().text(
            Components.translatable(nameKey + ".tooltip"),
            Components.literal(""),
            Components.literal("§l" + modeId),
            Components.translatable(modId + ".fullscreenTypes." + modeId + ".tooltip")
        ).build();
    }

    private static Option<Boolean> toggle(
        String key,
        boolean defaultValue,
        java.util.function.Supplier<Boolean> getter,
        java.util.function.Consumer<Boolean> setter,
        boolean restart
    ) {
        Option.Builder<Boolean> builder = Option.<Boolean>createBuilder()
            .name(Components.translatable(key))
            .description(OptionDescription.of(Components.translatable(key + ".tooltip")))
            .binding(defaultValue, getter, setter)
            .controller(TickBoxControllerBuilderImpl::new);
        if (restart) {
            builder.flag(OptionFlag.GAME_RESTART);
        }
        return builder.build();
    }
}
//?}
