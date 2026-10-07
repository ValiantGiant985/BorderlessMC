package dev.valiantgiant985.borderlessmc.compat.modmenu;

import dev.valiantgiant985.borderlessmc.BorderlessMC;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public final class NativeConfigScreen extends Screen {
    private final Screen parent;
    private int selectedTab;

    private Button generalTab;
    private Button advancedTab;
    private AbstractWidget pauseOnLostFocus;
    private AbstractWidget useDelayedFullscreen;
    private AbstractWidget useScaledFramebuffer;
    private AbstractWidget pauseOnLostFocusDuringMultiplayer;
    private Button pauseOnLostFocusReset;
    private Button useDelayedFullscreenReset;
    private Button useScaledFramebufferReset;
    private Button pauseOnLostFocusDuringMultiplayerReset;

    public NativeConfigScreen(Screen parent) {
        super(Component.translatable("borderlessmc.config.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int contentWidth = 310;
        int buttonHeight = 20;
        int gap = 5;
        int resetWidth = 30;
        int optionWidth = contentWidth - resetWidth - gap;
        int left = this.width / 2 - contentWidth / 2;
        int resetLeft = left + optionWidth + gap;
        int tabWidth = (contentWidth - gap) / 2;

        this.generalTab = this.addRenderableWidget(
            Button.builder(
                    Component.translatable("borderlessmc.config.general"),
                    button -> this.selectTab(0)
                )
                .bounds(left, 32, tabWidth, buttonHeight)
                .build()
        );

        this.advancedTab = this.addRenderableWidget(
            Button.builder(
                    Component.translatable("borderlessmc.config.advanced"),
                    button -> this.selectTab(1)
                )
                .bounds(left + tabWidth + gap, 32, tabWidth, buttonHeight)
                .build()
        );

        this.pauseOnLostFocus = this.addRenderableWidget(
            this.pauseOnLostFocusOption().createButton(this.options(), left, 72, optionWidth)
        );

        this.pauseOnLostFocusReset = this.addRenderableWidget(
            Button.builder(
                    Component.literal("↻").withStyle(ChatFormatting.BOLD),
                    button -> this.resetPauseOnLostFocus()
                )
                .bounds(resetLeft, 72, resetWidth, buttonHeight)
                .build()
        );

        this.useDelayedFullscreen = this.addRenderableWidget(
            this.useDelayedFullscreenOption().createButton(this.options(), left, 97, optionWidth)
        );

        this.useDelayedFullscreenReset = this.addRenderableWidget(
            Button.builder(
                    Component.literal("↻").withStyle(ChatFormatting.BOLD),
                    button -> this.resetUseDelayedFullscreen()
                )
                .bounds(resetLeft, 97, resetWidth, buttonHeight)
                .build()
        );

        this.useScaledFramebuffer = this.addRenderableWidget(
            this.useScaledFramebufferOption().createButton(this.options(), left, 72, optionWidth)
        );

        this.useScaledFramebufferReset = this.addRenderableWidget(
            Button.builder(
                    Component.literal("↻").withStyle(ChatFormatting.BOLD),
                    button -> this.resetUseScaledFramebuffer()
                )
                .bounds(resetLeft, 72, resetWidth, buttonHeight)
                .build()
        );

        this.pauseOnLostFocusDuringMultiplayer = this.addRenderableWidget(
            this.pauseOnLostFocusDuringMultiplayerOption().createButton(this.options(), left, 97, optionWidth)
        );

        this.pauseOnLostFocusDuringMultiplayerReset = this.addRenderableWidget(
            Button.builder(
                    Component.literal("↻").withStyle(ChatFormatting.BOLD),
                    button -> this.resetPauseOnLostFocusDuringMultiplayer()
                )
                .bounds(resetLeft, 97, resetWidth, buttonHeight)
                .build()
        );

        int bottomWidth = 145;
        int bottomStart = this.width / 2 - (bottomWidth * 2 + gap) / 2;

        this.addRenderableWidget(
            Button.builder(
                    Component.translatable("controls.reset"),
                    button -> this.reset()
                )
                .bounds(bottomStart, this.height - 28, bottomWidth, buttonHeight)
                .build()
        );

        this.addRenderableWidget(
            Button.builder(
                    CommonComponents.GUI_DONE,
                    button -> this.done()
                )
                .bounds(bottomStart + bottomWidth + gap, this.height - 28, bottomWidth, buttonHeight)
                .build()
        );

        this.updateTabVisibility();
        this.setInitialFocus(this.selectedTab == 0 ? this.generalTab : this.advancedTab);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        if (this.minecraft.level == null) {
            this.extractPanorama(graphics, delta);
        }

        this.extractBlurredBackground(graphics);
        this.extractMenuBackground(graphics);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        graphics.centeredText(this.font, this.title, this.width / 2, 12, 0xFFFFFFFF);
    }

    private net.minecraft.client.Options options() {
        return Minecraft.getInstance().options;
    }

    private void selectTab(int tab) {
        if (this.selectedTab == tab) {
            return;
        }

        this.selectedTab = tab;
        this.updateTabVisibility();
        this.setInitialFocus(tab == 0 ? this.generalTab : this.advancedTab);
    }

    private void updateTabVisibility() {
        boolean general = this.selectedTab == 0;

        this.generalTab.active = !general;
        this.advancedTab.active = general;

        this.pauseOnLostFocus.visible = general;
        this.pauseOnLostFocus.active = general;
        this.pauseOnLostFocusReset.visible = general;
        this.pauseOnLostFocusReset.active = general && this.options().pauseOnLostFocus;

        this.useDelayedFullscreen.visible = general;
        this.useDelayedFullscreen.active = general;
        this.useDelayedFullscreenReset.visible = general;
        this.useDelayedFullscreenReset.active = general && !BorderlessMC.CONFIG.getUseDelayedFullscreen();

        this.useScaledFramebuffer.visible = !general;
        this.useScaledFramebuffer.active = !general;
        this.useScaledFramebufferReset.visible = !general;
        this.useScaledFramebufferReset.active = !general && !BorderlessMC.CONFIG.getUseScaledFramebuffer();

        this.pauseOnLostFocusDuringMultiplayer.visible = !general;
        this.pauseOnLostFocusDuringMultiplayer.active = !general;
        this.pauseOnLostFocusDuringMultiplayerReset.visible = !general;
        this.pauseOnLostFocusDuringMultiplayerReset.active = !general && !BorderlessMC.CONFIG.getPauseOnLostFocusDuringMultiplayer();
    }

    private OptionInstance<Boolean> pauseOnLostFocusOption() {
        return OptionInstance.createBoolean(
            "borderlessmc.options.pauseOnLostFocus",
            tooltip("borderlessmc.options.pauseOnLostFocus.tooltip", false),
            this.options().pauseOnLostFocus,
            value -> {
                this.options().pauseOnLostFocus = value;
                this.updateResetButtonStates();
            }
        );
    }

    private OptionInstance<Boolean> useDelayedFullscreenOption() {
        return OptionInstance.createBoolean(
            "borderlessmc.options.useDelayedFullscreen",
            tooltip("borderlessmc.options.useDelayedFullscreen.tooltip", true),
            BorderlessMC.CONFIG.getUseDelayedFullscreen(),
            value -> {
                BorderlessMC.CONFIG.setUseDelayedFullscreen(value);
                this.updateResetButtonStates();
            }
        );
    }

    private OptionInstance<Boolean> useScaledFramebufferOption() {
        return OptionInstance.createBoolean(
            "borderlessmc.options.useScaledFramebuffer",
            tooltip("borderlessmc.options.useScaledFramebuffer.tooltip", true),
            BorderlessMC.CONFIG.getUseScaledFramebuffer(),
            value -> {
                BorderlessMC.CONFIG.setUseScaledFramebuffer(value);
                this.updateResetButtonStates();
            }
        );
    }

    private OptionInstance<Boolean> pauseOnLostFocusDuringMultiplayerOption() {
        return OptionInstance.createBoolean(
            "borderlessmc.options.pauseOnLostFocusDuringMultiplayer",
            tooltip("borderlessmc.options.pauseOnLostFocusDuringMultiplayer.tooltip", true),
            BorderlessMC.CONFIG.getPauseOnLostFocusDuringMultiplayer(),
            value -> {
                BorderlessMC.CONFIG.setPauseOnLostFocusDuringMultiplayer(value);
                this.updateResetButtonStates();
            }
        );
    }

    private static OptionInstance.TooltipSupplier<Boolean> tooltip(String key, boolean defaultValue) {
        Component defaultText = Component.translatable(defaultValue ? "borderlessmc.config.on" : "borderlessmc.config.off")
            .withStyle(defaultValue ? ChatFormatting.GREEN : ChatFormatting.RED);

        if ("en_ud".equals(Minecraft.getInstance().getLanguageManager().getSelected())) {
            return OptionInstance.cachedConstantTooltip(
                Component.empty()
                    .append(defaultText)
                    .append(Component.translatable("borderlessmc.config.default"))
                    .append("\n\n")
                    .append(Component.translatable(key))
            );
        }

        return OptionInstance.cachedConstantTooltip(
            Component.empty()
                .append(Component.translatable(key))
                .append("\n\n")
                .append(Component.translatable("borderlessmc.config.default"))
                .append(defaultText)
        );
    }

    private void updateResetButtonStates() {
        this.updateTabVisibility();
    }

    private void resetPauseOnLostFocus() {
        this.options().pauseOnLostFocus = false;
        this.refreshWidgets();
    }

    private void resetUseDelayedFullscreen() {
        BorderlessMC.CONFIG.setUseDelayedFullscreen(true);
        this.refreshWidgets();
    }

    private void resetUseScaledFramebuffer() {
        BorderlessMC.CONFIG.setUseScaledFramebuffer(true);
        this.refreshWidgets();
    }

    private void resetPauseOnLostFocusDuringMultiplayer() {
        BorderlessMC.CONFIG.setPauseOnLostFocusDuringMultiplayer(true);
        this.refreshWidgets();
    }

    private void reset() {
        this.options().pauseOnLostFocus = false;
        BorderlessMC.CONFIG.setUseDelayedFullscreen(true);
        BorderlessMC.CONFIG.setUseScaledFramebuffer(true);
        BorderlessMC.CONFIG.setPauseOnLostFocusDuringMultiplayer(true);
        this.refreshWidgets();
    }

    private void refreshWidgets() {
        this.clearWidgets();
        this.init();
    }

    private void done() {
        this.options().save();
        BorderlessMC.CONFIG.save();
        this.minecraft.setScreenAndShow(this.parent);
    }

    @Override
    public void onClose() {
        this.done();
    }
}
