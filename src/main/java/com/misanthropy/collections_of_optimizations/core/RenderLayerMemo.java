package com.misanthropy.collections_of_optimizations.core;

import net.minecraftforge.client.ChunkRenderTypeSet;

public final class RenderLayerMemo {

    public static volatile int version;

    public final ChunkRenderTypeSet layers;
    public final int stamp;

    public RenderLayerMemo(ChunkRenderTypeSet layers, int stamp) {
        this.layers = layers;
        this.stamp = stamp;
    }

    public interface Holder {

        RenderLayerMemo coo$renderLayerMemo();

        void coo$setRenderLayerMemo(RenderLayerMemo memo);
    }
}
