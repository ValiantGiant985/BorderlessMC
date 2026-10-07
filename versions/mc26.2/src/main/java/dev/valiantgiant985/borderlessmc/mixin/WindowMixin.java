package dev.valiantgiant985.borderlessmc.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.platform.Monitor;
import com.mojang.blaze3d.platform.MonitorManager;
import com.mojang.blaze3d.platform.VideoMode;
import com.mojang.blaze3d.platform.Window;
import dev.valiantgiant985.borderlessmc.BorderlessMC;
import dev.valiantgiant985.borderlessmc.BorderlessMCConfig;
import dev.valiantgiant985.borderlessmc.FullscreenManager;
import dev.valiantgiant985.borderlessmc.FullscreenMode;
import dev.valiantgiant985.borderlessmc.FullscreenType;
import dev.valiantgiant985.borderlessmc.FullscreenTypes;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Window.class)
abstract class WindowMixin implements FullscreenManager {
    @Shadow @Final private MonitorManager monitorManager;
    @Shadow private Optional<VideoMode> preferredFullscreenVideoMode;
    @Shadow private int windowedX;
    @Shadow private int windowedY;
    @Shadow private int windowedWidth;
    @Shadow private int windowedHeight;
    @Shadow private boolean fullscreen;
    @Shadow private boolean actuallyFullscreen;

    private boolean borderlessmc$borderless;
    private FullscreenMode borderlessmc$pendingMode;
    private FullscreenType borderlessmc$previousType;
    private FullscreenType borderlessmc$currentType;

    @Override
    public FullscreenMode getFullscreenMode() {
        if (this.borderlessmc$pendingMode != null) {
            return this.borderlessmc$pendingMode;
        }
        if (!this.fullscreen) {
            return FullscreenMode.OFF;
        }
        return this.borderlessmc$borderless ? FullscreenMode.BORDERLESS : FullscreenMode.ON;
    }

    @Override
    public void setFullscreenMode(FullscreenMode mode) {
        // Do not resize the GLFW window from inside the option widget callback.
        // Queue the transition onto Minecraft's client executor so it runs after
        // the current GUI input event has completed. This keeps the settings
        // screen interactive while still making fullscreen changes feel live.
        this.borderlessmc$pendingMode = mode;
        Minecraft.getInstance().execute(this::applyFullscreenMode);
    }

    @Override
    public void applyFullscreenMode() {
        if (this.borderlessmc$pendingMode == null) {
            return;
        }

        FullscreenMode mode = this.borderlessmc$pendingMode;
        this.borderlessmc$pendingMode = null;

        FullscreenMode oldMode = !this.fullscreen
            ? FullscreenMode.OFF
            : (this.borderlessmc$borderless ? FullscreenMode.BORDERLESS : FullscreenMode.ON);

        this.fullscreen = mode != FullscreenMode.OFF;
        if (this.fullscreen) {
            this.borderlessmc$borderless = mode == FullscreenMode.BORDERLESS;
        }

        this.actuallyFullscreen = (oldMode == mode) == this.fullscreen;

        Minecraft minecraft = Minecraft.getInstance();
        minecraft.options.fullscreen().set(this.fullscreen);
        minecraft.options.exclusiveFullscreen().set(true);

        ((Window)(Object)this).changeFullscreenVideoMode();
    }

    @Inject(method = "setMode", at = @At("HEAD"), cancellable = true)
    private void borderlessmc$applySelectedMode(CallbackInfo ci) {
        Window window = (Window)(Object)this;

        this.borderlessmc$previousType = this.borderlessmc$currentType;

        if (!this.fullscreen) {
            if (this.borderlessmc$previousType != null) {
                // Remove only the custom borderless attributes. Minecraft 26.2
                // owns the saved windowed rectangle and restores it in its native
                // setMode path; manually moving the GLFW window here overwrites
                // that restoration with stale fullscreen-sized coordinates.
                this.borderlessmc$previousType.disable(window);
            }

            this.borderlessmc$previousType = null;
            this.borderlessmc$currentType = null;

            // Keep requested/applied state different so vanilla actually executes
            // its fullscreen -> windowed restoration below.
            this.fullscreen = false;
            this.actuallyFullscreen = true;
            return;
        }

        BorderlessMCConfig config = BorderlessMC.CONFIG;
        FullscreenType requested = this.borderlessmc$borderless
            ? config.getBorderlessFullscreenType()
            : config.getFullscreenType();
        FullscreenType fallback = this.borderlessmc$borderless
            ? FullscreenTypes.borderless()
            : FullscreenTypes.exclusive();

        this.borderlessmc$currentType = FullscreenTypes.validate(requested, fallback);

        // minecraft:default is Minecraft's native exclusive path. Remove any
        // custom borderless attributes first, then deliberately leave vanilla's
        // requested/applied states different so it performs the monitor attach.
        if (this.borderlessmc$currentType == FullscreenTypes.exclusive()) {
            if (this.borderlessmc$previousType != null) {
                this.borderlessmc$previousType.disable(window);
            }

            GLFW.glfwSetWindowAttrib(window.handle(), GLFW.GLFW_AUTO_ICONIFY, GLFW.GLFW_TRUE);
            GLFW.glfwSetWindowAttrib(window.handle(), GLFW.GLFW_DECORATED, GLFW.GLFW_TRUE);
            GLFW.glfwWindowHint(GLFW.GLFW_SOFT_FULLSCREEN, GLFW.GLFW_FALSE);

            Minecraft.getInstance().options.exclusiveFullscreen().set(true);
            this.fullscreen = true;
            this.actuallyFullscreen = false;
            return;
        }

        Monitor monitor = this.monitorManager.findBestMonitor(window);
        if (monitor == null) {
            this.borderlessmc$currentType = null;
            return;
        }

        if (this.borderlessmc$previousType == null) {
            this.windowedX = window.x;
            this.windowedY = window.y;
            this.windowedWidth = window.width;
            this.windowedHeight = window.height;
        } else {
            this.borderlessmc$previousType.disable(window);
        }

        VideoMode videoMode = monitor.getPreferredVidMode(this.preferredFullscreenVideoMode);
        this.borderlessmc$currentType.enable(window, monitor, videoMode);
        this.borderlessmc$refreshSize();
        ci.cancel();
    }

    @Inject(
        method = "setMode",
        at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwSetWindowMonitor(JJIIIII)V", ordinal = 0)
    )
    private void borderlessmc$leaveCustomModeBeforeNativeFullscreen(CallbackInfo ci) {
        if (this.borderlessmc$previousType != null) {
            this.borderlessmc$previousType.disable((Window)(Object)this);
        }
    }

    @WrapOperation(
        method = "setMode",
        at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwGetWindowMonitor(J)J", ordinal = 0)
    )
    private long borderlessmc$treatCustomModeAsFullscreen(
        long handle,
        Operation<Long> original
    ) {
        return this.borderlessmc$previousType == null ? original.call(handle) : -1L;
    }

    @WrapOperation(
        method = "<init>",
        at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/MonitorManager;getMonitor(J)Lcom/mojang/blaze3d/platform/Monitor;")
    )
    private Monitor borderlessmc$prepareInitialWindow(
        MonitorManager manager,
        long handle,
        Operation<Monitor> original
    ) {
        // Start windowed so construction cannot steal focus. Delayed fullscreen
        // applies the configured mode after Minecraft has initialized.
        this.fullscreen = false;
        this.actuallyFullscreen = false;

        GLFW.glfwWindowHint(GLFW.GLFW_SOFT_FULLSCREEN, GLFW.GLFW_FALSE);
        Minecraft.getInstance().options.exclusiveFullscreen().set(true);

        return original.call(manager, handle);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void borderlessmc$applyConfiguredStartupMode(CallbackInfo ci) {
        FullscreenMode configuredMode = BorderlessMC.CONFIG.getFullscreenMode();
        if (configuredMode == FullscreenMode.OFF) {
            this.borderlessmc$borderless = false;
            this.borderlessmc$pendingMode = null;
            return;
        }

        // Construction itself stays windowed so GLFW/Minecraft can finish setting
        // up normally. Apply the saved mode immediately afterward through the same
        // deferred path used by the live Video Settings selector.
        this.borderlessmc$pendingMode = configuredMode;
        Minecraft.getInstance().execute(this::applyFullscreenMode);
    }

    private void borderlessmc$refreshSize() {
        Window window = (Window)(Object)this;
        int[] width = new int[1];
        int[] height = new int[1];
        GLFW.glfwGetWindowSize(window.handle(), width, height);
        window.width = Math.max(width[0], 1);
        window.height = Math.max(height[0], 1);
    }

    @Inject(method = "onMove", at = @At("TAIL"), require = 0)
    private void borderlessmc$rememberWindowedPosition(long handle, int x, int y, CallbackInfo ci) {
        if (!this.fullscreen && this.borderlessmc$currentType == null) {
            this.windowedX = x;
            this.windowedY = y;
        }
    }

    @Inject(method = "onResize", at = @At("TAIL"), require = 0)
    private void borderlessmc$rememberWindowedSize(long handle, int width, int height, CallbackInfo ci) {
        if (!this.fullscreen && this.borderlessmc$currentType == null && width > 0 && height > 0) {
            this.windowedWidth = width;
            this.windowedHeight = height;
        }
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void borderlessmc$save(CallbackInfo ci) {
        BorderlessMCConfig config = BorderlessMC.CONFIG;
        config.setFullscreenMode(this.getFullscreenMode());
        config.setPreferredFullscreenMode(
            this.borderlessmc$borderless ? FullscreenMode.BORDERLESS : FullscreenMode.ON
        );
        config.save();
    }
}
