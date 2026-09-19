package com.misanthropy.collections_of_optimizations.mixin.companions;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ModEntityFilter;
import dev.xylonity.companions.common.event.CompanionsServerEvents;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.lang.ref.Reference;
import java.util.Map;

@Mixin(value = CompanionsServerEvents.class, remap = false)
public abstract class MixinCompanionsServerEvents {

    @WrapOperation(
            method = "onEntityJoinLevelEvent",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"
            ),
            require = 0
    )
    private static Object coo$trackOnlyResolvableEntities(Map<Object, Object> tracker, Object key, Object value, Operation<Object> original) {
        if (CoOConfig.companionsLeanEntityTracker && value instanceof Reference<?> reference) {
            Object entity = reference.get();
            if (!(entity instanceof Player) && !ModEntityFilter.COMPANIONS.matches(entity)) {
                return null;
            }
        }
        return original.call(tracker, key, value);
    }
}
