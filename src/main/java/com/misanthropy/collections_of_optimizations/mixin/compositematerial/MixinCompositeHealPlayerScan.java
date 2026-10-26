package com.misanthropy.collections_of_optimizations.mixin.compositematerial;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import io.github.rcneg.compositematerial.common.events.HealEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mixin(value = HealEvents.class, remap = false)
public abstract class MixinCompositeHealPlayerScan {

    @WrapOperation(
            method = "onLivingHeal",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;m_45976_(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"),
            require = 0
    )
    private static List<LivingEntity> coo$playersOnly(Level level, Class<LivingEntity> type, AABB box,
                                                      Operation<List<LivingEntity>> original) {
        if (!CoOConfig.compositematerialHealPlayersOnly) {
            return original.call(level, type, box);
        }
        List<LivingEntity> found = null;
        for (Player player : level.players()) {
            if (!player.isSpectator() && player.getBoundingBox().intersects(box)) {
                if (found == null) {
                    found = new ArrayList<>(2);
                }
                found.add(player);
            }
        }
        return found == null ? Collections.emptyList() : found;
    }
}
