package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.core.EntityMeshReuse;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockEntity.class)
public abstract class MixinBlockEntityMeshHolder implements EntityMeshReuse.Holder {

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
