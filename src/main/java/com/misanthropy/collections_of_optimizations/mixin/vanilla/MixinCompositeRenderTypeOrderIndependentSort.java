package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.google.common.collect.ImmutableList;
import com.misanthropy.collections_of_optimizations.core.OrderIndependentSort;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(targets = "net.minecraft.client.renderer.RenderType$CompositeRenderType")
public abstract class MixinCompositeRenderTypeOrderIndependentSort extends RenderType implements OrderIndependentSort {

    @Shadow
    @Final
    private RenderType.CompositeState state;

    @Unique
    private byte coo$orderIndependentState;

    private MixinCompositeRenderTypeOrderIndependentSort(String name, VertexFormat format, VertexFormat.Mode mode,
                                                          int bufferSize, boolean affectsCrumbling, boolean sortOnUpload,
                                                          Runnable setupState, Runnable clearState) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
    }

    @Override
    public boolean coo$orderIndependent() {
        byte known = this.coo$orderIndependentState;
        if (known == 0) {
            ImmutableList<RenderStateShard> shards = ((RenderTypeCompositeStateAccessor) (Object) this.state).coo$states();
            boolean additive = shards.contains(ADDITIVE_TRANSPARENCY)
                    || shards.contains(LIGHTNING_TRANSPARENCY)
                    || shards.contains(GLINT_TRANSPARENCY);
            known = additive && shards.contains(COLOR_WRITE) ? (byte) 1 : (byte) 2;
            this.coo$orderIndependentState = known;
        }
        return known == 1;
    }
}
