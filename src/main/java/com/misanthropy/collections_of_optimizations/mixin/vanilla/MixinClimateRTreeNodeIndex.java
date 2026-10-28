package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.core.FlatClimateNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(targets = "net.minecraft.world.level.biome.Climate$RTree$Node")
public abstract class MixinClimateRTreeNodeIndex implements FlatClimateNode {
    @Unique
    private int coo$flatIndex = -1;

    @Override
    public int coo$flatIndex() {
        return coo$flatIndex;
    }

    @Override
    public void coo$setFlatIndex(int index) {
        coo$flatIndex = index;
    }
}
