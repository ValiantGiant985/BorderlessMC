package dev.valiantgiant985.borderlessmc.mixin;

import dev.valiantgiant985.borderlessmc.BorderlessMC;
import dev.valiantgiant985.borderlessmc.FullscreenMode;
import net.minecraft.client.main.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Arrays;

@Mixin(Main.class)
abstract class MainMixin {
    private static final String BORDERLESS_FLAG = "--borderless";

    @ModifyVariable(method = "main", at = @At("HEAD"), argsOnly = true, ordinal = 0, require = 0)
    private static String[] borderlessmc$consumeBorderlessFlag(String[] arguments) {
        boolean requested = Arrays.stream(arguments).anyMatch(BORDERLESS_FLAG::equals);
        if (!requested) {
            return arguments;
        }

        BorderlessMC.CONFIG.setFullscreenMode(FullscreenMode.BORDERLESS);
        return Arrays.stream(arguments)
            .filter(argument -> !BORDERLESS_FLAG.equals(argument))
            .toArray(String[]::new);
    }
}
