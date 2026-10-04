package com.misanthropy.collections_of_optimizations.mixin.cataclysm;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "com.github.L_Ender.cataclysm.entity.Deepling.AbstractDeepling", remap = false)
public abstract class MixinCataclysmDeeplingSwimRefresh {

    @Shadow
    public abstract boolean getDeeplingSwim();

    @WrapOperation(
            method = "m_8119_",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;m_45756_(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Z",
                    ordinal = 0),
            require = 0
    )
    private boolean coo$swimBranch(Level level, Entity self, AABB box, Operation<Boolean> original) {
        if (CoOConfig.cataclysmLeanDeeplingSwimRefresh && getDeeplingSwim() && coo$sizeSettled(self)) {
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
        if (CoOConfig.cataclysmLeanDeeplingSwimRefresh && !getDeeplingSwim() && coo$sizeSettled(self)) {
            return false;
        }
        return original.call(level, self, box);
    }

    @Unique
    private static boolean coo$sizeSettled(Entity e) {
        Pose pose = e.getPose();
        EntityDimensions want = e.getDimensions(pose);
        EntityDimensions have = ((MixinCataclysmEntityAccessor) e).coo$dimensions();
        return have.width == want.width && have.height == want.height && have.fixed == want.fixed
                && e.getEyeHeight() == e.getEyeHeightAccess(pose, want);
    }
}
