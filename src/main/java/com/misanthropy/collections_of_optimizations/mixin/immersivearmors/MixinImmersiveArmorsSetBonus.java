package com.misanthropy.collections_of_optimizations.mixin.immersivearmors;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "immersive_armors.armor_effects.ArmorEffect", remap = false)
public abstract class MixinImmersiveArmorsSetBonus {

    @Inject(
            method = "equippedTick",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void coo$skipClientSetBonusWrite(ItemStack stack, Level level, LivingEntity entity, int slot, CallbackInfo ci) {
        if (!CoOConfig.immersivearmorsSkipClientSetBonusWrite) {
            return;
        }
        if (level != null && level.isClientSide) {
            ci.cancel();
        }
    }
}
