package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.core.EntityMeshReuse;
import com.mojang.blaze3d.vertex.VertexBuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VertexBuffer.class)
public abstract class MixinVertexBufferMeshReuse {

    @Inject(method = {"draw", "drawWithShader"}, at = @At("HEAD"))
    private void coo$meshReuseDirectDraw(CallbackInfo ci) {
        EntityMeshReuse.directDraw();
    }
}
