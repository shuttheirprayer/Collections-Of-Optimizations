package com.misanthropy.collections_of_optimizations.mixin.brutality;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.goo.brutality.entity.capabilities.EntityCapabilities;
import net.goo.brutality.event.forge.ForgeEffectSyncHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ForgeEffectSyncHandler.class, remap = false)
public abstract class MixinBrutalityEffectSyncAudience {

    @Inject(
            method = {
                    "lambda$onAddEffect$0",
                    "lambda$onRemoveEffect$1",
                    "lambda$onExpiredEffect$2"
            },
            at = @At(value = "NEW", target = "net/goo/brutality/network/ClientboundSyncCapabilitiesPacket"),
            cancellable = true,
            require = 0
    )
    private static void coo$skipUnwatchedSync(MobEffect effect, LivingEntity entity,
                                              EntityCapabilities.EntityEffectCap cap, CallbackInfo ci) {
        if (!CoOConfig.brutalitySkipUnwatchedEffectSync || entity instanceof ServerPlayer
                || !(entity.level() instanceof ServerLevel level)) {
            return;
        }
        if (level.getChunkSource().chunkMap.getPlayers(entity.chunkPosition(), false).isEmpty()) {
            ci.cancel();
        }
    }
}
