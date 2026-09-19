package com.misanthropy.collections_of_optimizations.mixin.meetyourfight;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.api.SlotContext;

@Pseudo
@Mixin(targets = "lykrast.meetyourfight.item.SpectresEye", remap = false)
public abstract class MixinMyfSpectresEye {

    @Inject(method = "curioTick", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$serverOnlyGlowSweep(SlotContext context, ItemStack stack, CallbackInfo ci) {
        if (!CoOConfig.meetyourfightServerOnlyGlowSweep || context == null) {
            return;
        }
        LivingEntity entity = context.entity();
        if (entity != null && entity.level().isClientSide) {
            ci.cancel();
        }
    }
}
