package dev.valiantgiant985.borderlessmc.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.valiantgiant985.borderlessmc.BorderlessMC;
import dev.valiantgiant985.borderlessmc.FullscreenManager;
import dev.valiantgiant985.borderlessmc.FullscreenMode;
import dev.valiantgiant985.borderlessmc.util.Components;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.options.VideoSettingsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(VideoSettingsScreen.class)
abstract class VideoSettingsScreenMixin {
    @Inject(method = "removed", at = @At("TAIL"))
    private void borderlessmc$commitFullscreenSelection(CallbackInfo ci) {
        FullscreenManager.getInstance().applyFullscreenMode();
    }

    private static boolean borderlessmc$replaceFullscreenEntry(Object[] entries) {
        if (BorderlessMC.CONFIG.getBorderlessFullscreenType() == BorderlessMC.CONFIG.getFullscreenType()) {
            return false;
        }

        Object vanilla = Minecraft.getInstance().options.fullscreen();
        Object replacement = borderlessmc$fullscreenOption();
        for (int index = 0; index < entries.length; index++) {
            if (entries[index] == vanilla) {
                entries[index] = replacement;
                return true;
            }
        }
        return false;
    }

    //? if >=1.19 {
    @ModifyReturnValue(method = /*? >=26.1 {*/"displayOptions"/*?} else {*//*"options"*//*?}*/, at = @At("RETURN"))
    private static net.minecraft.client.OptionInstance<?>[] borderlessmc$displayOptions(net.minecraft.client.OptionInstance<?>[] original) {
        borderlessmc$replaceFullscreenEntry(original);

        //? if >=26.1 {
        List<net.minecraft.client.OptionInstance<?>> filtered = new ArrayList<>(Arrays.asList(original));
        filtered.remove(Minecraft.getInstance().options.exclusiveFullscreen());
        return filtered.toArray(Arrays.copyOf(original, filtered.size()));
        //?} else {
        /*return original;*/
        //?}
    }

    private static net.minecraft.client.OptionInstance<FullscreenMode> borderlessmc$fullscreenOption() {
        FullscreenManager controller = FullscreenManager.getInstance();
        return new net.minecraft.client.OptionInstance<>(
            "options.fullscreen",
            net.minecraft.client.OptionInstance.noTooltip(),
            (caption, mode) -> mode == FullscreenMode.BORDERLESS
                ? Components.literal("Borderless")
                : Components.translatable(mode == FullscreenMode.ON ? "options.on" : "options.off"),
            new net.minecraft.client.OptionInstance.Enum<>(
                List.of(FullscreenMode.values()),
                com.mojang.serialization.Codec.INT.xmap(FullscreenMode::get, FullscreenMode::getId)
            ),
            controller.getFullscreenMode(),
            controller::setFullscreenMode
        );
    }
    //?}
}
