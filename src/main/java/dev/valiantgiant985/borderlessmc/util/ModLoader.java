package dev.valiantgiant985.borderlessmc.util;

import java.nio.file.Path;

public abstract class ModLoader {
    private static final ModLoader INSTANCE = detect();

    private ModLoader() {
    }

    public static ModLoader getInstance() {
        return INSTANCE;
    }

    public abstract Path getConfigFolder();

    public abstract boolean isModLoaded(String modId);

    public abstract boolean isModLoaded(String modId, String minVersion);

    private static ModLoader detect() {
        //? if fabric {
        if (classPresent("net.fabricmc.loader.api.FabricLoader")) {
            return new FabricPlatform();
        }
        //?}
        //? if neoforge {
        if (classPresent("net.neoforged.fml.loading.FMLLoader")) {
            return new NeoForgePlatform();
        }
        //?}
        //? if forge {
        if (classPresent("net.minecraftforge.fml.ModList")) {
            return new ForgePlatform();
        }
        //?}
        throw new IllegalStateException("BorderlessMC could not identify the active mod loader");
    }

    private static boolean classPresent(String className) {
        try {
            Class.forName(className, false, ModLoader.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException | LinkageError ignored) {
            return false;
        }
    }

    //? if fabric {
    private static final class FabricPlatform extends ModLoader {
        @Override
        public Path getConfigFolder() {
            return net.fabricmc.loader.api.FabricLoader.getInstance()
                .getConfigDir()
                .toAbsolutePath()
                .normalize();
        }

        @Override
        public boolean isModLoaded(String modId) {
            return net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded(modId);
        }

        @Override
        public boolean isModLoaded(String modId, String minVersion) {
            try {
                var container = net.fabricmc.loader.api.FabricLoader.getInstance()
                    .getModContainer(modId)
                    .orElse(null);
                if (container == null) {
                    return false;
                }
                var required = net.fabricmc.loader.api.Version.parse(minVersion);
                return container.getMetadata().getVersion().compareTo(required) >= 0;
            } catch (net.fabricmc.loader.api.VersionParsingException exception) {
                throw new IllegalArgumentException("Invalid minimum version: " + minVersion, exception);
            }
        }
    }
    //?}

    //? if neoforge {
    private static final class NeoForgePlatform extends ModLoader {
        @Override
        public Path getConfigFolder() {
            return net.neoforged.fml.loading.FMLPaths.CONFIGDIR.get();
        }

        @Override
        public boolean isModLoaded(String modId) {
            return find(modId) != null;
        }

        @Override
        public boolean isModLoaded(String modId, String minVersion) {
            var mod = find(modId);
            return mod != null && compareVersions(mod.versionString(), minVersion) >= 0;
        }

        private net.neoforged.fml.loading.moddiscovery.ModFileInfo find(String modId) {
            return net.neoforged.fml.loading.FMLLoader.getCurrent()
                .getLoadingModList()
                .getModFileById(modId);
        }
    }
    //?}

    //? if forge {
    private static final class ForgePlatform extends ModLoader {
        @Override
        public Path getConfigFolder() {
            return net.minecraftforge.fml.loading.FMLPaths.CONFIGDIR.get();
        }

        @Override
        public boolean isModLoaded(String modId) {
            return net.minecraftforge.fml.ModList.getModFileById(modId) != null;
        }

        @Override
        public boolean isModLoaded(String modId, String minVersion) {
            var mod = net.minecraftforge.fml.ModList.getModFileById(modId);
            return mod != null && compareVersions(mod.versionString(), minVersion) >= 0;
        }
    }
    //?}

    private static int compareVersions(String installed, String required) {
        String[] left = installed.split("[^0-9]+");
        String[] right = required.split("[^0-9]+");
        int count = Math.max(left.length, right.length);

        for (int index = 0; index < count; index++) {
            int a = numericPart(left, index);
            int b = numericPart(right, index);
            if (a != b) {
                return Integer.compare(a, b);
            }
        }
        return 0;
    }

    private static int numericPart(String[] parts, int index) {
        if (index >= parts.length || parts[index].isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(parts[index]);
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }
}
