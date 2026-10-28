package com.misanthropy.collections_of_optimizations.mixin.goety;

import com.Polarice3.Goety.utils.EntityFinder;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(value = EntityFinder.class, remap = false)
public abstract class MixinEntityFinderSameLevel {
    @Inject(method = "getLivingEntityByUuiD(Lnet/minecraft/world/level/Level;Ljava/util/UUID;)Lnet/minecraft/world/entity/LivingEntity;",
            at = @At("HEAD"), cancellable = true)
    private static void coo$lookInOwnLevelFirst(Level level, UUID uuid, CallbackInfoReturnable<LivingEntity> cir) {
        if (CoOConfig.goetySameLevelOwnerLookup && uuid != null && level instanceof ServerLevel serverLevel) {
            Entity entity = serverLevel.getEntity(uuid);
            if (entity instanceof LivingEntity living) {
                cir.setReturnValue(living);
            }
        }
    }
}
