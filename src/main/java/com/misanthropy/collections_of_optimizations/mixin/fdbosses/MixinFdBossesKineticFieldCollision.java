package com.misanthropy.collections_of_optimizations.mixin.fdbosses;

import com.google.common.collect.ImmutableList;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.FdBossesPresence;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(targets = "com.finderfeed.fdbosses.client.BossCommonMixinHandle", remap = false)
public abstract class MixinFdBossesKineticFieldCollision {

    @Inject(
            method = "entityCollidersMixin",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$skipKineticFieldQuery(Entity entity, Level level, List<VoxelShape> shapes, AABB box,
                                                  CallbackInfoReturnable<Vec3> targetCallback,
                                                  ImmutableList.Builder<VoxelShape> builder, CallbackInfo ci) {
        if (CoOConfig.fdbossesSkipKineticFieldCollision && FdBossesPresence.noKineticFields(level)) {
            ci.cancel();
        }
    }
}
