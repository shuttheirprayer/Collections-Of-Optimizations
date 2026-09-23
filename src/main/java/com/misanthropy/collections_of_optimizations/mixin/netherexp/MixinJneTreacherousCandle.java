package com.misanthropy.collections_of_optimizations.mixin.netherexp;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.jadenxgamer.netherexp.registry.block.entity.TreacherousCandleBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = TreacherousCandleBlockEntity.class, remap = false)
public abstract class MixinJneTreacherousCandle {

    @Inject(method = "getPlayersInRadius", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$leanCandlePlayerScan(Level level, BlockPos pos, CallbackInfoReturnable<List<ServerPlayer>> cir) {
        if (!CoOConfig.netherexpLeanCandlePlayerScan) {
            return;
        }
        List<? extends Player> present = level.players();
        List<ServerPlayer> found = new ArrayList<>(present.size());
        if (!present.isEmpty()) {
            AABB box = new AABB(pos).inflate(16.0D, 16.0D, 16.0D);
            for (int i = 0; i < present.size(); i++) {
                Player player = present.get(i);
                if (player instanceof ServerPlayer serverPlayer
                        && !serverPlayer.isSpectator()
                        && serverPlayer.getBoundingBox().intersects(box)) {
                    found.add(serverPlayer);
                }
            }
        }
        cir.setReturnValue(found);
    }

    @WrapOperation(
            method = "updateBossBarPlayers",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;m_7292_(Lnet/minecraft/world/effect/MobEffectInstance;)Z"
            ),
            require = 0)
    private static boolean coo$throttleBetrayedRefresh(ServerPlayer player, MobEffectInstance instance, Operation<Boolean> original) {
        if (!CoOConfig.netherexpThrottleCandleEffectRefresh) {
            return original.call(player, instance);
        }
        MobEffectInstance current = player.getEffect(instance.getEffect());
        if (current != null
                && current.getAmplifier() >= instance.getAmplifier()
                && current.getDuration() > instance.getDuration() / 2) {
            return false;
        }
        return original.call(player, instance);
    }
}
