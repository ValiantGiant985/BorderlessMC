package dev.valiantgiant985.borderlessmc.util;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;

public final class MinecraftWindow {
    public static Window getInstance() {
        return Minecraft.getInstance().getWindow();
    }

    public static final class Windows {
        public static void concealCommandLine() {
        }

        public static void restoreCommandLine() {
        }

        public static void pleaseStopDiscardingFuckingFramesThankYou(Window window) {
        }

        public static void setStyle(Window window, long style, long exStyle) {
        }

        public static void restoreStyle(Window window) {
        }

        private Windows() {
        }
    }

    public static final class MacOS {
        public static void showGlobalUI() {
        }

        public static void hideGlobalUI() {
        }

        public static void setPresentationOptions(long presentationOptions) {
        }

        public static void setHasShadow(Window window, boolean hasShadow) {
        }

        public static void setResizable(Window window, boolean resizable) {
        }

        public static boolean registerWindowWillReturnFieldEditorStub(Window window) {
            return false;
        }

        private MacOS() {
        }
    }

    private MinecraftWindow() {
    }
}
