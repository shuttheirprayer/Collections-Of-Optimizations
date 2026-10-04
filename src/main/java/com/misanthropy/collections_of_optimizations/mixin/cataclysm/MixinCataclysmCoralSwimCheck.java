package com.misanthropy.collections_of_optimizations.mixin.cataclysm;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = {
        "com.github.L_Ender.cataclysm.entity.Deepling.Coral_Golem_Entity",
        "com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.Coralssus_Entity"
}, remap = false)
public abstract class MixinCataclysmCoralSwimCheck {

    @Shadow(remap = false)
    public abstract boolean getSwim();

    @WrapOperation(
            method = "m_8119_",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;m_45756_(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Z",
                    ordinal = 0),
            require = 0
    )
    private boolean coo$swimBranch(Level level, Entity self, AABB box, Operation<Boolean> original) {
        if (CoOConfig.cataclysmSwimCheckFirst && getSwim()) {
            return false;
        }
        return original.call(level, self, box);
    }

    @WrapOperation(
            method = "m_8119_",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;m_45756_(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Z",
                    ordinal = 1),
            require = 0
    )
    private boolean coo$walkBranch(Level level, Entity self, AABB box, Operation<Boolean> original) {
        if (CoOConfig.cataclysmSwimCheckFirst && !getSwim()) {
            return false;
        }
        return original.call(level, self, box);
    }
}
