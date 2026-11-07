package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.core.EntityMeshReuse;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Entity.class)
public abstract class MixinEntityMeshHolder implements EntityMeshReuse.Holder {

    @Unique
    private EntityMeshReuse.Cache coo$meshCache;

    @Override
    public EntityMeshReuse.Cache coo$meshCache() {
        EntityMeshReuse.Cache cache = coo$meshCache;
        if (cache == null) {
            cache = coo$meshCache = new EntityMeshReuse.Cache();
        }
        return cache;
    }
}
