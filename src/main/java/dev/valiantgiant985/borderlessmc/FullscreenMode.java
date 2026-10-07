package dev.valiantgiant985.borderlessmc;

public enum FullscreenMode {
    OFF(0, "options.off"),
    ON(1, "options.on"),
    BORDERLESS(2, "borderlessmc.options.borderless");

    private static final FullscreenMode[] BY_ID = values();

    private final int id;
    private final String translationKey;

    FullscreenMode(int id, String translationKey) {
        this.id = id;
        this.translationKey = translationKey;
    }

    public int getId() {
        return id;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public static FullscreenMode get(int id) {
        return BY_ID[Math.floorMod(id, BY_ID.length)];
    }
}
