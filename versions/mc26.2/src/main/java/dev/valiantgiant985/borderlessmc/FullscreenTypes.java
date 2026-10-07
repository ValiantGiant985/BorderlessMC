package dev.valiantgiant985.borderlessmc;

import com.mojang.blaze3d.platform.Monitor;
import com.mojang.blaze3d.platform.VideoMode;
import com.mojang.blaze3d.platform.Window;
import dev.valiantgiant985.borderlessmc.util.OS;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class FullscreenTypes {
    private static final FullscreenType DEFAULT = new DefaultFullscreen();
    private static final FullscreenType WINDOWED = new WindowedFullscreen();
    private static final FullscreenType WINDOWS_WINDOWED = new WindowsWindowedFullscreen();

    private static final Map<String, FullscreenType> REGISTRY = Stream.of(
        WINDOWS_WINDOWED, WINDOWED, DEFAULT
    ).collect(Collectors.toMap(FullscreenType::getId, x -> x, (x, _) -> x, LinkedHashMap::new));

    public static FullscreenType validate(FullscreenType fullscreenType) {
        return validate(fullscreenType, DEFAULT);
    }

    public static FullscreenType validate(FullscreenType fullscreenType, FullscreenType defaultFullscreenType) {
        if (fullscreenType == null || !fullscreenType.isSupported()) return validate(defaultFullscreenType, DEFAULT);
        return fullscreenType;
    }

    public static Optional<FullscreenType> get(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(REGISTRY.get(id.trim().toLowerCase(Locale.ROOT)));
    }

    public static Stream<FullscreenType> stream() {
        return REGISTRY.values().stream().filter(FullscreenType::isSupported);
    }

    public static FullscreenType exclusive() { return DEFAULT; }
    public static FullscreenType borderless() { return WINDOWED; }

    private static final class DefaultFullscreen implements FullscreenType {
        public String getId() { return "minecraft:default"; }
        public boolean isSupported() { return true; }
        public void enable(Window window, Monitor monitor, VideoMode videoMode) {}
        public void disable(Window window) {}
    }

    private static class WindowedFullscreen implements FullscreenType {
        public String getId() { return "minecraft:windowed"; }
        public boolean isSupported() { return true; }

        public void enable(Window window, Monitor monitor, VideoMode videoMode) {
            window.x = monitor.x();
            window.y = monitor.y();
            window.width = monitor.currentMode().getWidth();
            window.height = monitor.currentMode().getHeight();
            GLFW.glfwSetWindowAttrib(window.handle(), GLFW.GLFW_DECORATED, GLFW.GLFW_FALSE);
            GLFW.glfwSetWindowAttrib(window.handle(), GLFW.GLFW_AUTO_ICONIFY, GLFW.GLFW_FALSE);
            GLFW.glfwSetWindowMonitor(window.handle(), 0L, window.x, window.y, window.width, window.height, GLFW.GLFW_DONT_CARE);
        }

        public void disable(Window window) {
            GLFW.glfwSetWindowAttrib(window.handle(), GLFW.GLFW_DECORATED, GLFW.GLFW_TRUE);
            GLFW.glfwSetWindowAttrib(window.handle(), GLFW.GLFW_AUTO_ICONIFY, GLFW.GLFW_TRUE);
        }
    }

    private static final class WindowsWindowedFullscreen extends WindowedFullscreen {
        public String getId() { return "windows:windowed"; }
        public boolean isSupported() { return OS.isWindows(); }

        public void enable(Window window, Monitor monitor, VideoMode videoMode) {
            window.x = monitor.x();
            window.y = monitor.y();
            window.width = monitor.currentMode().getWidth();
            window.height = monitor.currentMode().getHeight() + 1;
            GLFW.glfwSetWindowAttrib(window.handle(), GLFW.GLFW_DECORATED, GLFW.GLFW_FALSE);
            GLFW.glfwSetWindowAttrib(window.handle(), GLFW.GLFW_AUTO_ICONIFY, GLFW.GLFW_FALSE);
            GLFW.glfwSetWindowMonitor(window.handle(), 0L, window.x, window.y, window.width, window.height, GLFW.GLFW_DONT_CARE);
        }

        public void disable(Window window) {
            GLFW.glfwSetWindowAttrib(window.handle(), GLFW.GLFW_DECORATED, GLFW.GLFW_TRUE);
            GLFW.glfwSetWindowAttrib(window.handle(), GLFW.GLFW_AUTO_ICONIFY, GLFW.GLFW_TRUE);
        }
    }

    private FullscreenTypes() {}
}
