package com.misanthropy.collections_of_optimizations.mixin.betterspellcasting;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.BscSwingStamp;
import com.misanthropy.collections_of_optimizations.core.BscSwingStampHolder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.kayn.better_spellcasting.event.AttackSpellHandler", remap = false)
public abstract class MixinBscPendingSpellExpiry {

    @Inject(
            method = "trackSwingServerSide(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/entity/LivingEntity;)V",
            at = @At("RETURN"),
            require = 0
    )
    private static void coo$stampSwing(ServerPlayer player, LivingEntity target, CallbackInfo ci) {
        if (!CoOConfig.betterspellcastingExpireQueuedSpell) {
            return;
        }
        if (player instanceof BscSwingStampHolder holder) {
            holder.coo$setBscSwingTick(player.level().getGameTime());
            BscSwingStamp.active = true;
        }
    }

    @Inject(
            method = "onLivingHurt(Lnet/minecraftforge/event/entity/living/LivingHurtEvent;)V",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$dropStaleQueuedSpell(LivingHurtEvent event, CallbackInfo ci) {
        if (!CoOConfig.betterspellcastingExpireQueuedSpell || !BscSwingStamp.active) {
            return;
        }
        Entity source = event.getSource().getEntity();
        if (!(source instanceof Player player) || player.level().isClientSide) {
            return;
        }
        if (!(player instanceof BscSwingStampHolder holder)) {
            return;
        }
        long stamp = holder.coo$bscSwingTick();
        if (stamp <= 0L || player.level().getGameTime() - stamp > CoOConfig.betterspellcastingQueuedSpellWindow) {
            ci.cancel();
        }
    }
}
