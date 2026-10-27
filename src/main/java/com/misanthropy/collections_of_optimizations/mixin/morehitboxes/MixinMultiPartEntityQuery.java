package com.misanthropy.collections_of_optimizations.mixin.morehitboxes;

import com.github.darkpred.morehitboxes.api.MultiPart;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Predicate;

@Mixin(value = Level.class, priority = 1500)
public abstract class MixinMultiPartEntityQuery {

    @WrapWithCondition(
            method = "getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfoReturnable;Ljava/util/List;)V",
                    remap = false
            ),
            remap = true,
            require = 0,
            expect = 0
    )
    private boolean coo$skipAbsentMultiPartFilter(Level level, Entity entity, AABB box, Predicate<Entity> predicate,
                                                  CallbackInfoReturnable<List<Entity>> cir, List<Entity> hits) {
        if (!CoOConfig.morehitboxesSkipAbsentMultiPartFilter) {
            return true;
        }

        for (int i = 0, n = hits.size(); i < n; i++) {
            if (hits.get(i) instanceof MultiPart) {
                return true;
            }
        }

        return false;
    }
}
