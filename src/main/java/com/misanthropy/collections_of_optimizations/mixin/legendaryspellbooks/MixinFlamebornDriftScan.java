package com.misanthropy.collections_of_optimizations.mixin.legendaryspellbooks;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Pseudo
@Mixin(targets = "dev.higurashi.legendary_spellbooks.common.effects.handler.FlamebornDriftSpellHandler", remap = false)
public abstract class MixinFlamebornDriftScan {

    @WrapOperation(
            method = "onPlayerTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/ClientLevel;getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;"
            ),
            remap = true,
            require = 0
    )
    private static List<Entity> coo$onlySpinningCasters(ClientLevel level, Entity except, AABB box,
                                                       Predicate<? super Entity> filter, Operation<List<Entity>> original) {
        if (!CoOConfig.legendaryspellbooksSpinningOnlyDriftScan) {
            return original.call(level, except, box, filter);
        }
        List<Entity> casters = List.of();
        for (Entity entity : level.entitiesForRendering()) {
            if (entity instanceof LivingEntity living && living.isAutoSpinAttack() && entity != except
                    && entity.getBoundingBox().intersects(box) && filter.test(entity)) {
                if (casters.isEmpty()) {
                    casters = new ArrayList<>(2);
                }
                casters.add(entity);
            }
        }
        return casters;
    }
}
