package com.misanthropy.collections_of_optimizations.mixin.emf;

import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ModelPart.class)
public interface EmfModelPartCubes {

    @Accessor("cubes")
    List<ModelPart.Cube> coo$cubes();
}
