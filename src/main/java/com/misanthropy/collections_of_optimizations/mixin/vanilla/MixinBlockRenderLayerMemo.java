package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.core.RenderLayerMemo;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Block.class)
public abstract class MixinBlockRenderLayerMemo implements RenderLayerMemo.Holder {

    @Unique
    private RenderLayerMemo coo$renderLayerMemo;

    @Override
    public RenderLayerMemo coo$renderLayerMemo() {
        return this.coo$renderLayerMemo;
    }

    @Override
    public void coo$setRenderLayerMemo(RenderLayerMemo memo) {
        this.coo$renderLayerMemo = memo;
    }
}
