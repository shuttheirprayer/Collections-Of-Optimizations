package com.misanthropy.collections_of_optimizations.mixin.oddaccessories;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.oddaccessories.item.CollectibleMealToyItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.api.SlotContext;

@Mixin(value = CollectibleMealToyItem.class, remap = false)
public abstract class MixinOddCollectibleMealToy {

    @Inject(method = "onCurioTick", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$gateMealToyScan(SlotContext slotContext, ItemStack stack, CallbackInfo ci) {
        if (!CoOConfig.oddaccessoriesGateMealToyScan) {
            return;
        }
        LivingEntity wearer = slotContext.entity();
        if (wearer == null) {
            return;
        }
        Level level = wearer.level();
        if (level.isClientSide) {
            return;
        }
        long gameTime = level.getGameTime();
        boolean repath = (gameTime + wearer.getId()) % 10L == 0L;
        boolean pulse = gameTime % 80L == 0L;
        if (!repath && !pulse) {
            ci.cancel();
        }
    }
}
