package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.IdleEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ArmorStand.class, priority = 3000)
public abstract class MixinArmorStandIdle implements IdleEntities.State {

    @Override
    public boolean coo$skipTick() {
        ArmorStand self = (ArmorStand) (Object) this;
        if (!IdleEntities.asleep(self)) {
            if ((self.tickCount & 7) != 0 || !CoOConfig.vanillaSleepIdleArmorStands || self.getClass() != ArmorStand.class || !this.coo$quiet(self)) {
                return false;
            }
            IdleEntities.trySleep(self, this.coo$hasGravity(self), false);
            if (!IdleEntities.asleep(self)) {
                return false;
            }
        }
        if (CoOConfig.vanillaSleepIdleArmorStands && this.coo$keepSleeping(self)) {
            return true;
        }
        this.coo$wake();
        return false;
    }

    @Inject(method = "interactAt", at = @At("HEAD"))
    private void coo$wakeOnInteract(Player player, Vec3 hit, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ((IdleEntities.State) this).coo$wake();
    }

    @Inject(method = "hurt", at = @At("HEAD"))
    private void coo$wakeOnHurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        ((IdleEntities.State) this).coo$wake();
    }

    @Inject(method = "setItemSlot", at = @At("HEAD"))
    private void coo$wakeOnEquip(EquipmentSlot slot, ItemStack stack, CallbackInfo ci) {
        ((IdleEntities.State) this).coo$wake();
    }

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void coo$wakeOnLoad(CompoundTag tag, CallbackInfo ci) {
        ((IdleEntities.State) this).coo$wake();
    }

    private boolean coo$hasGravity(ArmorStand self) {
        return !self.isNoGravity() && !self.isMarker();
    }

    private boolean coo$quiet(ArmorStand self) {
        return self.isAlive() && self.hurtTime == 0 && self.deathTime == 0 && self.getActiveEffectsMap().isEmpty()
                && self.getArrowCount() == 0 && self.getAirSupply() >= self.getMaxAirSupply();
    }

    private boolean coo$keepSleeping(ArmorStand self) {
        if (!IdleEntities.stillIdle(self)) {
            return false;
        }
        if (((self.tickCount + self.getId()) & 3) == 0
                && (!IdleEntities.settled(self, this.coo$hasGravity(self), false) || !this.coo$quiet(self))) {
            return false;
        }
        return true;
    }
}
