package com.misanthropy.collections_of_optimizations.mixin.sophisticatedbackpacks;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.mixin.vanilla.EntityPersistentDataAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.p3pp3rf1y.sophisticatedbackpacks.common.EntityBackpackAdditionHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityBackpackAdditionHandler.class, remap = false)
public abstract class MixinSbpEntityBackpackAdditions {

    @Unique
    private static boolean coo$hasBackpackData(LivingEntity entity) {
        CompoundTag data = ((EntityPersistentDataAccessor) entity).coo$persistentData();
        return data != null && data.contains("sophisticatedbackpacks", Tag.TAG_COMPOUND);
    }

    @Inject(method = "onLivingUpdate", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$skipEntitiesWithoutBackpackData(LivingEvent.LivingTickEvent event, CallbackInfo ci) {
        if (!CoOConfig.sophisticatedbackpacksSkipEntityDataChecks) {
            return;
        }
        LivingEntity entity = event.getEntity();
        if (entity != null && !coo$hasBackpackData(entity)) {
            ci.cancel();
        }
    }

    @Inject(method = "removeBackpackUuid", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$skipLeaveWithoutBackpackData(Monster entity, Level level, CallbackInfo ci) {
        if (!CoOConfig.sophisticatedbackpacksSkipEntityDataChecks) {
            return;
        }
        if (entity != null && !coo$hasBackpackData(entity)) {
            ci.cancel();
        }
    }
}
