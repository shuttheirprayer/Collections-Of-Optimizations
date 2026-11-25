package com.misanthropy.collections_of_optimizations.mixin.emf;

import com.misanthropy.collections_of_optimizations.core.CubeVertices;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(targets = "traben.entity_model_features.models.parts.EMFModelPartCustom$EMFCube", remap = false)
public abstract class MixinEmfCubeVertices implements CubeVertices.Holder {

    @Unique
    private CubeVertices coo$vertices;

    @Override
    public CubeVertices coo$cubeVertices() {
        CubeVertices vertices = this.coo$vertices;
        if (vertices == null) {
            this.coo$vertices = vertices = CubeVertices.capture((ModelPart.Cube) (Object) this);
        }
        return vertices;
    }
}
