package com.misanthropy.collections_of_optimizations.mixin.immediatelyfast;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.mixin.vanilla.BufferSourceFixedBuffersAccessor;
import com.mojang.blaze3d.vertex.BufferBuilder;
import it.unimi.dsi.fastutil.objects.ReferenceLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import net.minecraft.client.renderer.RenderType;
import net.raphimc.immediatelyfast.feature.core.BatchableBufferSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

@Mixin(value = BatchableBufferSource.class, remap = false)
public abstract class MixinBatchableBufferSource {

    @Shadow
    protected Map<RenderType, ReferenceSet<BufferBuilder>> fallbackBuffers;

    @Shadow
    @Final
    protected Set<RenderType> activeLayers;

    @Shadow
    protected abstract BufferBuilder addNewFallbackBuffer(RenderType layer);

    @Unique
    private final ReferenceLinkedOpenHashSet<RenderType> coo$busyLayers = new ReferenceLinkedOpenHashSet<>();

    @Redirect(
            method = "m_109911_()V",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;keySet()Ljava/util/Set;"),
            require = 0
    )
    private Set<RenderType> coo$onlyBusyLayers(Map<RenderType, BufferBuilder> fixedBuffers) {
        Set<RenderType> layers = fixedBuffers.keySet();
        if (!CoOConfig.immediatelyfastSkipIdleLayers) {
            return layers;
        }
        Set<RenderType> active = this.activeLayers;
        Map<RenderType, ReferenceSet<BufferBuilder>> fallback = this.fallbackBuffers;
        if (active.size() + fallback.size() >= layers.size()) {
            return layers;
        }
        ReferenceLinkedOpenHashSet<RenderType> busy = this.coo$busyLayers;
        busy.clear();
        boolean checkFallback = !fallback.isEmpty();
        int remaining = active.size() + fallback.size();
        for (RenderType layer : layers) {
            if (active.contains(layer) || (checkFallback && fallback.containsKey(layer))) {
                busy.add(layer);
                if (--remaining == 0) {
                    break;
                }
            }
        }
        return busy;
    }

    @Inject(
            method = "getOrCreateBufferBuilder",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void coo$singleLookupGetOrCreate(RenderType layer, CallbackInfoReturnable<BufferBuilder> cir) {
        if (!CoOConfig.immediatelyfastSingleBufferLookup) {
            return;
        }
        if (!layer.canConsolidateConsecutiveGeometry()) {
            cir.setReturnValue(this.addNewFallbackBuffer(layer));
            return;
        }
        BufferBuilder fixed = ((BufferSourceFixedBuffersAccessor) this).coo$fixedBuffers().get(layer);
        if (fixed != null) {
            cir.setReturnValue(fixed);
            return;
        }
        ReferenceSet<BufferBuilder> fallback = this.fallbackBuffers.get(layer);
        cir.setReturnValue(fallback != null ? coo$firstBuffer(fallback) : this.addNewFallbackBuffer(layer));
    }

    @Unique
    private static BufferBuilder coo$firstBuffer(ReferenceSet<BufferBuilder> buffers) {
        return buffers instanceof ReferenceLinkedOpenHashSet<BufferBuilder> linked
                ? linked.first()
                : buffers.iterator().next();
    }

    @Inject(
            method = "getBufferBuilder",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void coo$singleLookupGetBuffer(RenderType layer, CallbackInfoReturnable<Set<BufferBuilder>> cir) {
        if (!CoOConfig.immediatelyfastSingleBufferLookup) {
            return;
        }
        Map<RenderType, ReferenceSet<BufferBuilder>> fallbackBuffers = this.fallbackBuffers;
        if (!fallbackBuffers.isEmpty()) {
            ReferenceSet<BufferBuilder> fallback = fallbackBuffers.get(layer);
            if (fallback != null) {
                cir.setReturnValue(fallback);
                return;
            }
        }
        BufferBuilder fixed = ((BufferSourceFixedBuffersAccessor) this).coo$fixedBuffers().get(layer);
        cir.setReturnValue(fixed != null ? Collections.singleton(fixed) : Collections.emptySet());
    }
}
