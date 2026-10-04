package com.misanthropy.collections_of_optimizations.mixin.cataclysm;

import com.github.L_Ender.cataclysm.client.render.etc.LightningRender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = LightningRender.class, remap = false)
public interface CataclysmLightningRenderAccessor {

    @Accessor("boltOwners")
    Map<Object, ?> coo$boltOwners();
}
