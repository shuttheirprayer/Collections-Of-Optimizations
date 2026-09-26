package com.misanthropy.collections_of_optimizations.mixin.snuffles;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Pseudo
@Mixin(targets = "mod.schnappdragon.snuffles.common.block.FrostyFluffCarpetBlock", remap = false)
public abstract class MixinFrostyFluffCarpetTouch {

    @WrapOperation(
            method = "m_7892_",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;m_45933_(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"
            ),
            require = 0
    )
    private List<Entity> coo$leanCarpetTouch(Level level, Entity except, AABB box, Operation<List<Entity>> original, @Local(argsOnly = true) Entity entity) {
        if (!CoOConfig.snufflesLeanCarpetTouchCheck || entity == null || except != null) {
            return original.call(level, except, box);
        }
        return box.intersects(entity.getBoundingBox()) ? List.of(entity) : List.of();
    }
}
