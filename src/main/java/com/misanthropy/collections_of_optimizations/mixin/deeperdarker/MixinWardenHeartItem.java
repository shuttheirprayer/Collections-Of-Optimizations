package com.misanthropy.collections_of_optimizations.mixin.deeperdarker;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.DeeperDarkerRefs;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "com.kyanite.deeperdarker.content.items.WardenHeartItem", remap = false)
public abstract class MixinWardenHeartItem {

    @WrapOperation(
            method = "m_6883_(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;IZ)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;m_216327_()Lnet/minecraft/util/RandomSource;"),
            require = 0
    )
    private RandomSource coo$reuseHeartRandom(Operation<RandomSource> original) {
        if (!CoOConfig.deeperdarkerReuseHeartRandom) {
            return original.call();
        }
        return DeeperDarkerRefs.scratchRandom();
    }
}
