package dev.valiantgiant985.borderlessmc.util;

import java.util.Locale;

public final class OS {
    private enum Family {
        WINDOWS,
        MACOS,
        UNIX,
        OTHER
    }

    private static final Family CURRENT = detect();

    private OS() {
    }

    public static boolean isWindows() {
        return CURRENT == Family.WINDOWS;
    }

    public static boolean isUnix() {
        return CURRENT == Family.UNIX || CURRENT == Family.MACOS;
    }

    public static boolean isMacOS() {
        return CURRENT == Family.MACOS;
    }

    private static Family detect() {
        String name = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        if (name.contains("win")) {
            return Family.WINDOWS;
        }
        if (name.contains("mac") || name.contains("darwin")) {
            return Family.MACOS;
        }
        if (name.contains("nix") || name.contains("nux") || name.contains("aix")
            || name.contains("bsd") || name.contains("sunos")) {
            return Family.UNIX;
        }
        return Family.OTHER;
    }
}
