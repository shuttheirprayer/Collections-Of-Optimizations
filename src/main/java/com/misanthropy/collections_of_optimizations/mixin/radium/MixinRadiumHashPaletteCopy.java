package com.misanthropy.collections_of_optimizations.mixin.radium;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "me.jellysquid.mods.lithium.common.world.chunk.LithiumHashPalette", remap = false)
public abstract class MixinRadiumHashPaletteCopy {

    @WrapOperation(
            method = "m_199814_()Lnet/minecraft/world/level/chunk/Palette;",
            at = @At(
                    value = "NEW",
                    target = "(Lit/unimi/dsi/fastutil/objects/Reference2IntMap;)Lit/unimi/dsi/fastutil/objects/Reference2IntOpenHashMap;"
            ),
            require = 0
    )
    private Reference2IntOpenHashMap<Object> coo$keepAbsentMarker(Reference2IntMap<Object> source,
                                                                  Operation<Reference2IntOpenHashMap<Object>> original) {
        Reference2IntOpenHashMap<Object> copy = original.call(source);
        if (CoOConfig.radiumFixHashPaletteCopy) {
            copy.defaultReturnValue(source.defaultReturnValue());
        }
        return copy;
    }
}
