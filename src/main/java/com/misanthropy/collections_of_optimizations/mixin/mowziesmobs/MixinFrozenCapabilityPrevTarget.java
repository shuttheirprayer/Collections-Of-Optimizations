package com.misanthropy.collections_of_optimizations.mixin.mowziesmobs;

import com.bobmowzie.mowziesmobs.server.capability.FrozenCapability;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.UUID;

@Mixin(value = FrozenCapability.FrozenCapabilityImp.class, remap = false)
public abstract class MixinFrozenCapabilityPrevTarget {

    @WrapOperation(
            method = "deserializeNBT(Lnet/minecraft/nbt/CompoundTag;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;getUUID(Ljava/lang/String;)Ljava/util/UUID;", remap = true),
            require = 0
    )
    private UUID coo$readPrevTargetIfPresent(CompoundTag tag, String key, Operation<UUID> original) {
        if (CoOConfig.mowziesmobsSkipMissingPrevTargetThrow && tag.get(key) == null) {
            return null;
        }
        return original.call(tag, key);
    }

    @WrapWithCondition(
            method = "deserializeNBT(Lnet/minecraft/nbt/CompoundTag;)V",
            at = @At(value = "INVOKE", target = "Lcom/bobmowzie/mowziesmobs/server/capability/FrozenCapability$FrozenCapabilityImp;setPreAttackTarget(Ljava/util/UUID;)V"),
            require = 0
    )
    private boolean coo$keepTargetWhenMissing(FrozenCapability.FrozenCapabilityImp self, UUID target) {
        return target != null || !CoOConfig.mowziesmobsSkipMissingPrevTargetThrow;
    }
}
