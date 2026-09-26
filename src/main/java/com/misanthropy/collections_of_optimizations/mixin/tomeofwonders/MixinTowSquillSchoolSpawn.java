package com.misanthropy.collections_of_optimizations.mixin.tomeofwonders;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.platypushasnohat.tome_of_wonders.entities.Squill", remap = false)
public abstract class MixinTowSquillSchoolSpawn {

    @Unique
    private ServerLevelAccessor coo$worldgenLevel;

    @Inject(method = "m_6518_", at = @At("HEAD"), require = 0)
    private void coo$captureSpawnLevel(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, SpawnGroupData spawnData, CompoundTag tag, CallbackInfoReturnable<SpawnGroupData> cir) {
        this.coo$worldgenLevel = CoOConfig.tomeofwondersSafeSquillSchoolWorldgen && !(level instanceof Level) ? level : null;
    }

    @Inject(method = "m_6518_", at = @At("RETURN"), require = 0)
    private void coo$releaseSpawnLevel(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, SpawnGroupData spawnData, CompoundTag tag, CallbackInfoReturnable<SpawnGroupData> cir) {
        this.coo$worldgenLevel = null;
    }

    @WrapOperation(
            method = "spawnSchool",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;m_7967_(Lnet/minecraft/world/entity/Entity;)Z"
            ),
            require = 0
    )
    private boolean coo$addToWorldgenRegion(Level instance, Entity entity, Operation<Boolean> original) {
        ServerLevelAccessor region = this.coo$worldgenLevel;
        if (region == null) {
            return original.call(instance, entity);
        }
        if (!region.hasChunk(SectionPos.blockToSectionCoord(entity.getBlockX()), SectionPos.blockToSectionCoord(entity.getBlockZ()))) {
            return false;
        }
        return region.addFreshEntity(entity);
    }
}
