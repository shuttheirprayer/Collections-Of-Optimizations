package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.SingleValuePalette;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PalettedContainer.class)
public abstract class MixinPalettedContainerUniform<T> {

    @Shadow
    private volatile PalettedContainer.Data<T> data;

    @Overwrite
    protected T get(int index) {
        PalettedContainer.Data<T> current = this.data;
        if (current.palette() instanceof SingleValuePalette<T> palette && palette.value != null && CoOConfig.vanillaUniformSectionLookup) {
            return palette.value;
        }
        return current.palette().valueFor(current.storage().get(index));
    }
}
