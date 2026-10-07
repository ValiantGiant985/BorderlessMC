//? if cloth-config: >0.0.0 {
package dev.valiantgiant985.borderlessmc.compat.clothconfig;

import dev.valiantgiant985.borderlessmc.BorderlessMCConfig;
import dev.valiantgiant985.borderlessmc.FullscreenType;
import dev.valiantgiant985.borderlessmc.FullscreenTypes;
import dev.valiantgiant985.borderlessmc.util.Components;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

final class ClothConfigScreenImpl {
    private ClothConfigScreenImpl() {
    }

    static Screen create(BorderlessMCConfig config, String modId, Screen parent) {
        Minecraft client = Minecraft.getInstance();
        Component title = Components.translatable("modmenu.nameTranslation." + modId);
        ConfigBuilder screen = ConfigBuilder.create().setParentScreen(parent).setTitle(title);
        ConfigCategory category = screen.getOrCreateCategory(title);
        ConfigEntryBuilder entries = screen.entryBuilder();

        List<String> modes = FullscreenTypes.stream().map(FullscreenType::getId).toList();
        FullscreenType exclusive = FullscreenTypes.validate(config.getFullscreenType(), FullscreenTypes.exclusive());
        FullscreenType borderless = FullscreenTypes.validate(config.getBorderlessFullscreenType(), FullscreenTypes.borderless());

        category.addEntry(entries.startStringDropdownMenu(
                Components.translatable(modId + ".options.fullscreenType"), exclusive.getId())
            .setSelections(modes)
            .setDefaultValue(FullscreenTypes.exclusive().getId())
            .setSaveConsumer(id -> FullscreenTypes.get(id).ifPresent(config::setFullscreenType))
            .requireRestart()
            .setSuggestionMode(false)
            .build());

        category.addEntry(entries.startStringDropdownMenu(
                Components.translatable(modId + ".options.borderlessFullscreenType"), borderless.getId())
            .setSelections(modes)
            .setDefaultValue(FullscreenTypes.borderless().getId())
            .setSaveConsumer(id -> FullscreenTypes.get(id).ifPresent(config::setBorderlessFullscreenType))
            .requireRestart()
            .setSuggestionMode(false)
            .build());

        addToggle(entries, category, modId + ".options.useDelayedFullscreen",
            config.getUseDelayedFullscreen(), true, config::setUseDelayedFullscreen, true);
        addToggle(entries, category, modId + ".options.useScaledFramebuffer",
            config.getUseScaledFramebuffer(), true, config::setUseScaledFramebuffer, true);
        addToggle(entries, category, modId + ".options.pauseOnLostFocus",
            client.options.pauseOnLostFocus, false, value -> client.options.pauseOnLostFocus = value, false);
        addToggle(entries, category, modId + ".options.pauseOnLostFocusDuringMultiplayer",
            config.getPauseOnLostFocusDuringMultiplayer(), true,
            config::setPauseOnLostFocusDuringMultiplayer, false);

        return screen.build();
    }

    private static void addToggle(
        ConfigEntryBuilder entries,
        ConfigCategory category,
        String key,
        boolean current,
        boolean defaultValue,
        java.util.function.Consumer<Boolean> save,
        boolean restart
    ) {
        var builder = entries.startBooleanToggle(Components.translatable(key), current)
            .setDefaultValue(defaultValue)
            .setSaveConsumer(save);
        if (restart) {
            builder.requireRestart();
        }
        category.addEntry(builder.build());
    }
}
//?}
