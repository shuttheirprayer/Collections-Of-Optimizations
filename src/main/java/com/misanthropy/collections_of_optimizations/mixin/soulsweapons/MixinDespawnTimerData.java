package com.misanthropy.collections_of_optimizations.mixin.soulsweapons;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.EntityNbtMemo;
import com.misanthropy.collections_of_optimizations.mixin.vanilla.EntityPersistentDataAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.soulsweaponry.entitydata.DespawnTimerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = DespawnTimerData.class, remap = false)
public abstract class MixinDespawnTimerData {

    @Inject(
            method = "getDespawnTicks",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$readWithoutWriting(Entity entity, CallbackInfoReturnable<Integer> cir) {
        if (!CoOConfig.soulsweaponsLeanDespawnTimer) {
            return;
        }
        if (entity instanceof Player) {
            cir.setReturnValue(0);
            return;
        }
        CompoundTag data = ((EntityPersistentDataAccessor) entity).coo$persistentData();
        if (data == null) {
            cir.setReturnValue(0);
            return;
        }
        EntityNbtMemo memo = (EntityNbtMemo) entity;
        if (CoOConfig.soulsweaponsRememberNoDespawnTimer && memo.coo$noDespawnTimerTag() == data) {
            cir.setReturnValue(0);
            return;
        }
        int ticks = data.getInt(DespawnTimerData.DESPAWN_ID);
        if (ticks == 0) {
            memo.coo$noDespawnTimerTag(data);
        }
        cir.setReturnValue(ticks);
    }

    @Inject(method = {"addDespawnTicks", "setDespawnTicks"}, at = @At("HEAD"), require = 0)
    private static void coo$forgetNoDespawnTimer(Entity entity, int amount, CallbackInfoReturnable<Integer> cir) {
        ((EntityNbtMemo) entity).coo$noDespawnTimerTag(null);
    }
}
