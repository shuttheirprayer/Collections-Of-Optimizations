package com.misanthropy.collections_of_optimizations.mixin.goetydelight;

import com.misanthropy.collections_of_optimizations.core.VisualEffectLatch;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.v_black_cat.goetydelight.visual.EntityVisualEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityVisualEffects.class, remap = false)
public abstract class MixinEntityVisualEffects {

    @Inject(
            method = "add(Lnet/minecraft/resources/ResourceLocation;ILnet/minecraft/nbt/CompoundTag;J)V",
            at = @At("RETURN"),
            require = 1
    )
    private void coo$armOnAdd(ResourceLocation id, int durationTicks, CompoundTag data, long gameTime, CallbackInfo ci) {
        VisualEffectLatch.arm();
    }

    @Inject(
            method = "deserializeNBT(Lnet/minecraft/nbt/CompoundTag;J)V",
            at = @At("RETURN"),
            require = 1
    )
    private void coo$armOnDeserialize(CompoundTag tag, long gameTime, CallbackInfo ci) {
        VisualEffectLatch.arm();
    }
}
