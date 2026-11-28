package com.misanthropy.collections_of_optimizations.mixin.citadel;

import com.github.alexthe666.citadel.server.entity.pathfinding.raycoms.pathjobs.AbstractPathJob;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.lang.ref.WeakReference;

@Mixin(value = AbstractPathJob.class, remap = false)
public interface CitadelPathJobAccess {

    @Accessor("entity")
    WeakReference<LivingEntity> coo$entity();
}
