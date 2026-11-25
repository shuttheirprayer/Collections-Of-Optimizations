package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.PaletteStorage;
import net.minecraft.world.level.chunk.PalettedContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(PalettedContainer.class)
public abstract class MixinPalettedContainerGetAll<T> {

    @Shadow
    private volatile PalettedContainer.Data<T> data;

    @Inject(method = "getAll", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$listWithMask(Consumer<T> consumer, CallbackInfo ci) {
        PalettedContainer.Data<T> current = this.data;
        if (CoOConfig.vanillaListPaletteWithMask && PaletteStorage.forEachValue(current.palette(), current.storage(), consumer)) {
            ci.cancel();
        }
    }
}
