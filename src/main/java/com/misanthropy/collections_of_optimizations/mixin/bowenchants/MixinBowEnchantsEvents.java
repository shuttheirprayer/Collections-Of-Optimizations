package com.misanthropy.collections_of_optimizations.mixin.bowenchants;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.mixin.vanilla.EntityPersistentDataAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.doug.bowenchants.events.BowEnchantsEvents", remap = false)
public abstract class MixinBowEnchantsEvents {

    @Inject(method = "onEntityJoinLevel", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$noEnhancedPowerReapply(EntityJoinLevelEvent event, CallbackInfo ci) {
        if (!CoOConfig.bowenchantsNoEnhancedPowerReapply) return;
        if (!(event.getEntity() instanceof Projectile projectile) || event.getLevel().isClientSide()) return;
        if (event.loadedFromDisk()) {
            ci.cancel();
            return;
        }
        CompoundTag data = ((EntityPersistentDataAccessor) projectile).coo$persistentData();
        if (data != null && data.getBoolean("bowenchants.enhanced_power_arrow_applied")) {
            ci.cancel();
        }
    }

    @Inject(method = "applyDrawSpeedAdjustment", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$drawSpeedRangedOnly(LivingEntityUseItemEvent.Tick event, Player player, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (!CoOConfig.bowenchantsDrawSpeedRangedOnly) return;
        if (stack.getItem() instanceof ProjectileWeaponItem) return;
        UseAnim anim = stack.getUseAnimation();
        if (anim == UseAnim.BOW || anim == UseAnim.CROSSBOW) return;
        cir.setReturnValue(event.getDuration());
    }
}
