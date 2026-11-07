package com.misanthropy.collections_of_optimizations.mixin.goetycataclysm;

import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ModelPart.class)
public interface GcModelPartAccessor {

    @Accessor("children")
    Map<String, ModelPart> coo$gcChildren();
}
