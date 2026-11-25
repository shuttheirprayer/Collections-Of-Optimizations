package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.PaletteStorage;
import net.minecraft.util.BitStorage;
import net.minecraft.world.level.chunk.Palette;
import net.minecraft.world.level.chunk.PalettedContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PalettedContainer.Data.class)
public abstract class MixinPalettedContainerDataCopy<T> {

    @Shadow
    @Final
    private BitStorage storage;

    @Shadow
    @Final
    private Palette<T> palette;

    @Inject(method = "copyFrom", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$copyByPaletteEntry(Palette<T> from, BitStorage source, CallbackInfo ci) {
        if (CoOConfig.vanillaCopyPalettesByEntry && PaletteStorage.copy(from, source, this.palette, this.storage)) {
            ci.cancel();
        }
    }
}
