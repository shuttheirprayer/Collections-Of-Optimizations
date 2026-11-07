package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.sugar.Local;
import com.misanthropy.collections_of_optimizations.core.SpreadEntityTicks;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LevelRenderer.class)
public abstract class MixinLevelRendererSpreadPartialTick {

    @ModifyVariable(method = "renderEntity", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float coo$spreadPartialTick(float partialTick, @Local(argsOnly = true) Entity entity) {
        return SpreadEntityTicks.partialTick(entity, partialTick);
    }
}
