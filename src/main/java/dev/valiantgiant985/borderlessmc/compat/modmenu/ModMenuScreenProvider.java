package dev.valiantgiant985.borderlessmc.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.valiantgiant985.borderlessmc.BorderlessMC;

public final class ModMenuScreenProvider implements ModMenuApi {
    private static final ConfigScreenFactory<?> FACTORY = BorderlessMC::createConfigScreen;

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return FACTORY;
    }
}
