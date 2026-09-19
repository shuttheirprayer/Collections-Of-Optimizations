package com.misanthropy.collections_of_optimizations.mixin.mobgrindingutils;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.MguChickenTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "mob_grinding_utils.events.RenderChickenSwell", remap = false)
public abstract class MixinMguChickenSwellTag {

    @WrapOperation(
            method = "renderChickenSwell",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;getPersistentData()Lnet/minecraft/nbt/CompoundTag;",
                    ordinal = 0
            ),
            require = 0
    )
    private CompoundTag coo$readExistingSwellTag(LivingEntity entity, Operation<CompoundTag> original) {
        if (!CoOConfig.mobgrindingutilsLeanChickenTag) {
            return original.call(entity);
        }
        CompoundTag existing = MguChickenTag.existing(entity);
        return existing != null ? existing : original.call(entity);
    }
}
