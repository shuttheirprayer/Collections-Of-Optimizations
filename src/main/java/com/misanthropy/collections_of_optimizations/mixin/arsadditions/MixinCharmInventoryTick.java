package com.misanthropy.collections_of_optimizations.mixin.arsadditions;

import com.github.jarva.arsadditions.common.item.curios.Charm;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Charm.class, remap = false)
public abstract class MixinCharmInventoryTick {

    @Inject(method = "m_6883_", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$skipStowedCharmTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (!CoOConfig.arsadditionsSkipStowedCharmTick) {
            return;
        }
        if (entity instanceof LivingEntity living
                && (living.getMainHandItem() == stack || living.getOffhandItem() == stack)) {
            return;
        }
        ci.cancel();
    }
}
