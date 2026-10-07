package dev.valiantgiant985.borderlessmc.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.valiantgiant985.borderlessmc.BorderlessMC;
import dev.valiantgiant985.borderlessmc.FullscreenManager;
import dev.valiantgiant985.borderlessmc.FullscreenMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(/*? >=26.2 {*/net.minecraft.client.gui.Gui/*?} else {*//*Minecraft*//*?}*/.class)
abstract class GuiMixin {
    private static boolean borderlessmc$startupModeHandled;

    //? if <26.2 {
    /*@WrapOperation(method = "pauseGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;setScreen(Lnet/minecraft/client/gui/screens/Screen;)V", ordinal = 1), require = 0)
    private void borderlessmc$filterMultiplayerPause(Minecraft client, Screen screen, Operation<Void> original) {
        if (client.isWindowActive() || BorderlessMC.CONFIG.getPauseOnLostFocusDuringMultiplayer()) {
            original.call(client, screen);
        }
    }*/
    //?} else {
    @WrapOperation(method = "setPauseScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;setScreen(Lnet/minecraft/client/gui/screens/Screen;)V", ordinal = 1), require = 0)
    private void borderlessmc$filterMultiplayerPause(net.minecraft.client.gui.Gui gui, Screen screen, Operation<Void> original) {
        Minecraft client = Minecraft.getInstance();
        if (client.isWindowActive() || BorderlessMC.CONFIG.getPauseOnLostFocusDuringMultiplayer()) {
            original.call(gui, screen);
        }
    }
    //?}

    @Inject(method = "setOverlay", at = @At("RETURN"), require = 0)
    private void borderlessmc$finishStartupFullscreen(Overlay overlay, CallbackInfo ci) {
        if (overlay != null || borderlessmc$startupModeHandled) {
            return;
        }

        borderlessmc$startupModeHandled = true;
        if (!BorderlessMC.CONFIG.getUseDelayedFullscreen()) {
            return;
        }

        FullscreenMode requested = BorderlessMC.CONFIG.getFullscreenMode();
        FullscreenManager controller = FullscreenManager.getInstance();
        if (requested != FullscreenMode.OFF && controller.getFullscreenMode() == FullscreenMode.OFF) {
            controller.setFullscreenMode(requested);
        }
    }
}
