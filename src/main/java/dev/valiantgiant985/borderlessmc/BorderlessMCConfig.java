package dev.valiantgiant985.borderlessmc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.Expose;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dev.valiantgiant985.borderlessmc.util.ModLoader;
import dev.valiantgiant985.borderlessmc.util.OS;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class BorderlessMCConfig {
    private transient Path storagePath;
    private transient boolean dirty;

    @Expose private FullscreenMode fullscreenMode;
    @Expose private FullscreenMode preferredFullscreenMode;
    @Expose private FullscreenType fullscreenType;
    @Expose private FullscreenType borderlessFullscreenType;
    @Expose private Boolean useDelayedFullscreen;
    @Expose private Boolean useScaledFramebuffer;
    @Expose private Boolean pauseOnLostFocusDuringMultiplayer;

    @Expose private @Nullable BorderlessMCConfig linux;
    @Expose private @Nullable BorderlessMCConfig macos;
    @Expose private @Nullable BorderlessMCConfig windows;

    private BorderlessMCConfig() {
    }

    public static BorderlessMCConfig load(String filePath) {
        Path path = Path.of(filePath);
        BorderlessMCConfig config = read(path);
        if (config == null) {
            config = new BorderlessMCConfig();
            config.dirty = true;
        }
        config.storagePath = path;
        config.fillDefaults();
        return config;
    }

    public static BorderlessMCConfig loadById(String id) {
        Path path = ModLoader.getInstance().getConfigFolder().resolve(id + ".json");
        return load(path.toString());
    }

    private static @Nullable BorderlessMCConfig read(Path path) {
        if (!Files.isRegularFile(path)) {
            return null;
        }
        try (Reader reader = Files.newBufferedReader(path)) {
            return GSON.fromJson(reader, BorderlessMCConfig.class);
        } catch (Exception ignored) {
            return null;
        }
    }

    public FullscreenMode getFullscreenMode() {
        return value(ConfigValue.FULLSCREEN_MODE);
    }

    public void setFullscreenMode(FullscreenMode value) {
        update(ConfigValue.FULLSCREEN_MODE, value);
    }

    public FullscreenMode getPreferredFullscreenMode() {
        return value(ConfigValue.PREFERRED_MODE);
    }

    public void setPreferredFullscreenMode(FullscreenMode value) {
        update(ConfigValue.PREFERRED_MODE, value);
    }

    public FullscreenType getFullscreenType() {
        return value(ConfigValue.FULLSCREEN_TYPE);
    }

    public void setFullscreenType(FullscreenType value) {
        update(ConfigValue.FULLSCREEN_TYPE, value);
    }

    public FullscreenType getBorderlessFullscreenType() {
        return value(ConfigValue.BORDERLESS_TYPE);
    }

    public void setBorderlessFullscreenType(FullscreenType value) {
        update(ConfigValue.BORDERLESS_TYPE, value);
    }

    public boolean getUseDelayedFullscreen() {
        return value(ConfigValue.DELAYED_FULLSCREEN);
    }

    public void setUseDelayedFullscreen(boolean value) {
        update(ConfigValue.DELAYED_FULLSCREEN, value);
    }

    public boolean getUseScaledFramebuffer() {
        return value(ConfigValue.SCALED_FRAMEBUFFER);
    }

    public void setUseScaledFramebuffer(boolean value) {
        update(ConfigValue.SCALED_FRAMEBUFFER, value);
    }

    public boolean getPauseOnLostFocusDuringMultiplayer() {
        return value(ConfigValue.MULTIPLAYER_PAUSE);
    }

    public void setPauseOnLostFocusDuringMultiplayer(boolean value) {
        update(ConfigValue.MULTIPLAYER_PAUSE, value);
    }

    public void save() {
        if (!dirty || storagePath == null) {
            return;
        }
        try {
            Path parent = storagePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            try (Writer writer = Files.newBufferedWriter(storagePath)) {
                GSON.toJson(this, writer);
            }
            dirty = false;
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to save BorderlessMC configuration", exception);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T value(ConfigValue key) {
        BorderlessMCConfig platform = platformConfig(false);
        Object platformValue = platform == null ? null : key.read(platform);
        Object baseValue = key.read(this);
        return (T)(platformValue != null ? platformValue : baseValue);
    }

    private <T> void update(ConfigValue key, T newValue) {
        Object current = value(key);
        if (java.util.Objects.equals(current, newValue)) {
            return;
        }

        BorderlessMCConfig destination = platformConfig(true);
        key.write(destination == null ? this : destination, newValue);
        fillDefaults();
        dirty = true;
    }

    private @Nullable BorderlessMCConfig platformConfig(boolean forWrite) {
        boolean hasOverrides = linux != null || macos != null || windows != null;
        boolean createOverride = forWrite && hasOverrides;

        if (OS.isWindows()) {
            if (windows == null && createOverride) windows = new BorderlessMCConfig();
            return windows;
        }
        if (OS.isMacOS()) {
            if (macos == null && createOverride) macos = new BorderlessMCConfig();
            return macos;
        }
        if (OS.isUnix()) {
            if (linux == null && createOverride) linux = new BorderlessMCConfig();
            return linux;
        }
        return null;
    }

    private void fillDefaults() {
        if (fullscreenMode == null) fullscreenMode = FullscreenMode.OFF;
        if (preferredFullscreenMode == null) preferredFullscreenMode = FullscreenMode.BORDERLESS;
        if (fullscreenType == null) fullscreenType = FullscreenTypes.exclusive();
        if (borderlessFullscreenType == null) borderlessFullscreenType = FullscreenTypes.borderless();
        if (useDelayedFullscreen == null) useDelayedFullscreen = Boolean.TRUE;
        if (useScaledFramebuffer == null) useScaledFramebuffer = Boolean.TRUE;
        if (pauseOnLostFocusDuringMultiplayer == null) pauseOnLostFocusDuringMultiplayer = Boolean.TRUE;
    }

    private enum ConfigValue {
        FULLSCREEN_MODE {
            Object read(BorderlessMCConfig c) { return c.fullscreenMode; }
            void write(BorderlessMCConfig c, Object v) { c.fullscreenMode = (FullscreenMode)v; }
        },
        PREFERRED_MODE {
            Object read(BorderlessMCConfig c) { return c.preferredFullscreenMode; }
            void write(BorderlessMCConfig c, Object v) { c.preferredFullscreenMode = (FullscreenMode)v; }
        },
        FULLSCREEN_TYPE {
            Object read(BorderlessMCConfig c) { return c.fullscreenType; }
            void write(BorderlessMCConfig c, Object v) { c.fullscreenType = (FullscreenType)v; }
        },
        BORDERLESS_TYPE {
            Object read(BorderlessMCConfig c) { return c.borderlessFullscreenType; }
            void write(BorderlessMCConfig c, Object v) { c.borderlessFullscreenType = (FullscreenType)v; }
        },
        DELAYED_FULLSCREEN {
            Object read(BorderlessMCConfig c) { return c.useDelayedFullscreen; }
            void write(BorderlessMCConfig c, Object v) { c.useDelayedFullscreen = (Boolean)v; }
        },
        SCALED_FRAMEBUFFER {
            Object read(BorderlessMCConfig c) { return c.useScaledFramebuffer; }
            void write(BorderlessMCConfig c, Object v) { c.useScaledFramebuffer = (Boolean)v; }
        },
        MULTIPLAYER_PAUSE {
            Object read(BorderlessMCConfig c) { return c.pauseOnLostFocusDuringMultiplayer; }
            void write(BorderlessMCConfig c, Object v) { c.pauseOnLostFocusDuringMultiplayer = (Boolean)v; }
        };

        abstract Object read(BorderlessMCConfig config);
        abstract void write(BorderlessMCConfig config, Object value);
    }

    @SuppressWarnings("deprecation")
    private static final Gson GSON = new GsonBuilder()
        .setLenient()
        .setPrettyPrinting()
        .excludeFieldsWithoutExposeAnnotation()
        .registerTypeAdapter(FullscreenType.class, new TypeAdapter<FullscreenType>() {
            @Override
            public void write(JsonWriter out, FullscreenType value) throws IOException {
                if (value == null) {
                    out.nullValue();
                } else {
                    out.value(value.getId());
                }
            }

            @Override
            public FullscreenType read(JsonReader in) throws IOException {
                if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                    in.nextNull();
                    return null;
                }
                return FullscreenTypes.get(in.nextString()).orElse(null);
            }
        })
        .create();
}
