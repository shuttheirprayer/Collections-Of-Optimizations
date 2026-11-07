package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.core.EntityMeshReuse;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderType.class)
public abstract class MixinRenderTypeMeshReuse {

    @Inject(method = "end", at = @At("HEAD"))
    private void coo$meshReuseFlushBegin(CallbackInfo ci) {
        EntityMeshReuse.flushBegin();
    }

    @Inject(method = "end", at = @At("RETURN"))
    private void coo$meshReuseFlushEnd(CallbackInfo ci) {
        EntityMeshReuse.flushEnd();
    }
}
