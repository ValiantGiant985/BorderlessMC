package dev.valiantgiant985.borderlessmc;

import com.mojang.blaze3d.platform.Monitor;
import com.mojang.blaze3d.platform.VideoMode;
import com.mojang.blaze3d.platform.Window;
import dev.valiantgiant985.borderlessmc.util.OS;
//? >=26.3 {
import org.lwjgl.sdl.SDLVideo;
//?} else {
/*import org.lwjgl.glfw.GLFW;*/
//?}

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
        WINDOWS_WINDOWED,
        WINDOWED,
        DEFAULT
    ).collect(Collectors.toMap(
        FullscreenType::getId,
        x -> x,
        (x, _) -> x,
        LinkedHashMap::new
    ));

    public static FullscreenType validate(FullscreenType fullscreenType) {
        return validate(fullscreenType, DEFAULT);
    }

    public static FullscreenType validate(FullscreenType fullscreenType, FullscreenType defaultFullscreenType) {
        if (fullscreenType == null || !fullscreenType.isSupported()) {
            return validate(defaultFullscreenType, DEFAULT);
        }
        return fullscreenType;
    }

    public static Optional<FullscreenType> get(String id) {
        if (id == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(REGISTRY.get(id.trim().toLowerCase(Locale.ROOT)));
    }

    public static Stream<FullscreenType> stream() {
        return REGISTRY.values().stream().filter(FullscreenType::isSupported);
    }

    public static FullscreenType exclusive() {
        return DEFAULT;
    }

    public static FullscreenType borderless() {
        return OS.isWindows() ? WINDOWS_WINDOWED : WINDOWED;
    }

    private static final class DefaultFullscreen implements FullscreenType {
        @Override
        public String getId() {
            return "minecraft:default";
        }

        @Override
        public boolean isSupported() {
            return true;
        }

        @Override
        public void enable(Window window, Monitor monitor, VideoMode videoMode) {
        }

        @Override
        public void disable(Window window) {
        }
    }

    private static class WindowedFullscreen implements FullscreenType {
        @Override
        public String getId() {
            return "minecraft:windowed";
        }

        @Override
        public boolean isSupported() {
            return true;
        }

        @Override
        public void enable(Window window, Monitor monitor, VideoMode videoMode) {
            //? >=26.3 {
            SDLVideo.SDL_SetWindowFullscreen(window.handle(), false);
            //?} else {
            /*GLFW.glfwSetWindowMonitor(window.handle(), 0L, window.x, window.y, window.width, window.height, GLFW.GLFW_DONT_CARE);*/
            //?}
            //? >=26.3 {
            SDLVideo.SDL_SyncWindow(window.handle());
            //?}
            //? >=26.3 {
            SDLVideo.SDL_SetWindowBordered(window.handle(), false);
            //?} else {
            /*GLFW.glfwSetWindowAttrib(window.handle(), GLFW.GLFW_DECORATED, GLFW.GLFW_FALSE);*/
            //?}
            window.x = monitor.x();
            window.y = monitor.y();
            window.width = monitor.currentMode().getWidth();
            window.height = monitor.currentMode().getHeight();
            //? >=26.3 {
            SDLVideo.SDL_SetWindowPosition(window.handle(), window.x, window.y);
            //?} else {
            /*GLFW.glfwSetWindowPos(window.handle(), window.x, window.y);*/
            //?}
            //? >=26.3 {
            SDLVideo.SDL_SetWindowSize(window.handle(), window.width, window.height);
            //?} else {
            /*GLFW.glfwSetWindowSize(window.handle(), window.width, window.height);*/
            //?}
            //? >=26.3 {
            SDLVideo.SDL_SyncWindow(window.handle());
            //?}
        }

        @Override
        public void disable(Window window) {
            //? >=26.3 {
            SDLVideo.SDL_SetWindowBordered(window.handle(), true);
            //?} else {
            /*GLFW.glfwSetWindowAttrib(window.handle(), GLFW.GLFW_DECORATED, GLFW.GLFW_TRUE);*/
            //?}
        }
    }

    private static final class WindowsWindowedFullscreen extends WindowedFullscreen {
        @Override
        public String getId() {
            return "windows:windowed";
        }

        @Override
        public boolean isSupported() {
            return OS.isWindows();
        }

        @Override
        public void enable(Window window, Monitor monitor, VideoMode videoMode) {
            //? >=26.3 {
            SDLVideo.SDL_SetWindowBordered(window.handle(), false);
            //?} else {
            /*GLFW.glfwSetWindowAttrib(window.handle(), GLFW.GLFW_DECORATED, GLFW.GLFW_FALSE);*/
            //?}
            //? >=26.3 {
            SDLVideo.SDL_SetWindowFullscreen(window.handle(), false);
            //?} else {
            /*GLFW.glfwSetWindowMonitor(window.handle(), 0L, window.x, window.y, window.width, window.height, GLFW.GLFW_DONT_CARE);*/
            //?}
            window.x = monitor.x();
            window.y = monitor.y();
            window.width = monitor.currentMode().getWidth();
            window.height = monitor.currentMode().getHeight() + 1;
            //? >=26.3 {
            SDLVideo.SDL_SetWindowPosition(window.handle(), window.x, window.y);
            //?} else {
            /*GLFW.glfwSetWindowPos(window.handle(), window.x, window.y);*/
            //?}
            //? >=26.3 {
            SDLVideo.SDL_SetWindowSize(window.handle(), window.width, window.height);
            //?} else {
            /*GLFW.glfwSetWindowSize(window.handle(), window.width, window.height);*/
            //?}
            //? >=26.3 {
            SDLVideo.SDL_SyncWindow(window.handle());
            //?}
        }

        @Override
        public void disable(Window window) {
            //? >=26.3 {
            SDLVideo.SDL_SetWindowBordered(window.handle(), true);
            //?} else {
            /*GLFW.glfwSetWindowAttrib(window.handle(), GLFW.GLFW_DECORATED, GLFW.GLFW_TRUE);*/
            //?}
        }
    }

    private FullscreenTypes() {
    }
}
