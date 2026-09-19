package com.misanthropy.collections_of_optimizations.mixin.peyroscythe;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collections;
import java.util.List;

@Pseudo
@Mixin(targets = "com.rinko1231.peyroscythe.effect.BlackFlameWingsEffect", remap = false)
public abstract class MixinPeyroBlackFlameWings {

    @WrapOperation(
            method = "m_6742_",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;m_45933_(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"
            ),
            require = 0
    )
    private List<Entity> coo$skipClientWingContactSweep(Level level, Entity self, AABB box, Operation<List<Entity>> original) {
        if (CoOConfig.peyroscytheBlackFlameWingsServerOnlyContact && level != null && level.isClientSide) {
            return Collections.emptyList();
        }
        return original.call(level, self, box);
    }
}
