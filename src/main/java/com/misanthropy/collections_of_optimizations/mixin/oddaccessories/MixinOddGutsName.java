package com.misanthropy.collections_of_optimizations.mixin.oddaccessories;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.oddaccessories.item.GutsItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GutsItem.class, remap = false)
public abstract class MixinOddGutsName {

    @Inject(method = "updateName", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$throttleGutsName(ItemStack stack, Entity entity, CallbackInfo ci) {
        int interval = CoOConfig.oddaccessoriesNameUpdateInterval;
        if (interval > 1 && entity != null && entity.tickCount % interval != 0) {
            ci.cancel();
        }
    }
}
