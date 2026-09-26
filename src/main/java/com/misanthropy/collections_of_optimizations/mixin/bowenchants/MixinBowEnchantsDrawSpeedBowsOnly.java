package com.misanthropy.collections_of_optimizations.mixin.bowenchants;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.doug.bowenchants.events.BowEnchantsEvents", remap = false)
public abstract class MixinBowEnchantsDrawSpeedBowsOnly {

    @Inject(method = "applyDrawSpeedAdjustment", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$onlyBows(LivingEntityUseItemEvent.Tick event, Player player, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (CoOConfig.bowenchantsDrawSpeedBowsOnly && !(stack.getItem() instanceof ProjectileWeaponItem) && stack.getUseAnimation() != UseAnim.BOW) {
            cir.setReturnValue(event.getDuration());
        }
    }
}
