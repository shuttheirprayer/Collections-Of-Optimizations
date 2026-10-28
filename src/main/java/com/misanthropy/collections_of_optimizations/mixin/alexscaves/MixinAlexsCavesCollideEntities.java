package com.misanthropy.collections_of_optimizations.mixin.alexscaves;

import com.bawnorton.mixinsquared.TargetHandler;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.RadiumEntityCollisions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(value = Entity.class, priority = 1500)
public abstract class MixinAlexsCavesCollideEntities {

    @TargetHandler(
            mixin = "com.github.alexmodguy.alexscaves.mixin.EntityMixin",
            name = "ac_collide"
    )
    @Redirect(
            method = "@MixinSquared:Handler",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;m_183134_(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"),
            require = 0,
            remap = false
    )
    private List<VoxelShape> coo$radiumEntityCollisions(Level level, Entity entity, AABB box) {
        if (CoOConfig.alexscavesRadiumEntityCollisions) {
            return RadiumEntityCollisions.collisions(level, entity, box);
        }
        return level.getEntityCollisions(entity, box);
    }
}
