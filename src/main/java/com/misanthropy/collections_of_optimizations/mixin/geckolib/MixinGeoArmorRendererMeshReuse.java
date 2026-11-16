package com.misanthropy.collections_of_optimizations.mixin.geckolib;

import com.misanthropy.collections_of_optimizations.core.EntityMeshReuse;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

@Mixin(GeoArmorRenderer.class)
public abstract class MixinGeoArmorRendererMeshReuse {

    @ModifyVariable(method = "renderToBuffer", at = @At("STORE"), ordinal = 0, require = 0)
    private MultiBufferSource coo$recordArmorIntoMeshReuse(MultiBufferSource source) {
        return EntityMeshReuse.recorderFor(source);
    }
}
