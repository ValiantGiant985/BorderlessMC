package dev.valiantgiant985.borderlessmc.mixin;

import com.mojang.blaze3d.platform.Window;
import dev.valiantgiant985.borderlessmc.BorderlessMC;
import dev.valiantgiant985.borderlessmc.FullscreenManager;
import dev.valiantgiant985.borderlessmc.FullscreenMode;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
abstract class WindowMixin implements FullscreenManager {
    @Shadow
    private boolean fullscreen;

    @Shadow
    private boolean exclusiveFullscreen;

    @Shadow
    private void setMode() {
    }

    @Override
    public FullscreenMode getFullscreenMode() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft != null && minecraft.options != null) {
            if (!minecraft.options.fullscreen().get()) {
                return FullscreenMode.OFF;
            }

            return minecraft.options.exclusiveFullscreen().get()
                ? FullscreenMode.ON
                : FullscreenMode.BORDERLESS;
        }

        if (!this.fullscreen) {
            return FullscreenMode.OFF;
        }

        return this.exclusiveFullscreen ? FullscreenMode.ON : FullscreenMode.BORDERLESS;
    }

    @Override
    public void setFullscreenMode(FullscreenMode fullscreenMode) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.options.fullscreen().set(fullscreenMode != FullscreenMode.OFF);
        minecraft.options.exclusiveFullscreen().set(fullscreenMode == FullscreenMode.ON);
    }

    @Override
    public void applyFullscreenMode() {
        this.setMode();
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void save(CallbackInfo ci) {
        BorderlessMC.CONFIG.setFullscreenMode(this.getFullscreenMode());
        BorderlessMC.CONFIG.setPreferredFullscreenMode(this.getFullscreenMode());
        BorderlessMC.CONFIG.save();
    }
}
