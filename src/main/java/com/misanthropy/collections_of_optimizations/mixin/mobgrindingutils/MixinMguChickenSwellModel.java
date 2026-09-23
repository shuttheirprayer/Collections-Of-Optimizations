package com.misanthropy.collections_of_optimizations.mixin.mobgrindingutils;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "mob_grinding_utils.events.RenderChickenSwell", remap = false)
public abstract class MixinMguChickenSwellModel {

    @Unique
    private static LayerDefinition coo$swellLayer;

    @Unique
    private static ModelPart coo$swellRoot;

    @WrapOperation(
            method = "renderChickenSwell",
            at = @At(
                    value = "INVOKE",
                    target = "Lmob_grinding_utils/models/ChickenBodyModel;createBodyLayer()Lnet/minecraft/client/model/geom/builders/LayerDefinition;"
            ),
            require = 0
    )
    private LayerDefinition coo$reuseSwellLayer(Operation<LayerDefinition> original) {
        if (!CoOConfig.mobgrindingutilsCacheChickenSwellModel) {
            return original.call();
        }
        LayerDefinition cached = coo$swellLayer;
        if (cached == null) {
            cached = original.call();
            coo$swellLayer = cached;
        }
        return cached;
    }

    @WrapOperation(
            method = "renderChickenSwell",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/geom/builders/LayerDefinition;bakeRoot()Lnet/minecraft/client/model/geom/ModelPart;",
                    remap = true
            ),
            require = 0
    )
    private ModelPart coo$reuseSwellRoot(LayerDefinition definition, Operation<ModelPart> original) {
        if (!CoOConfig.mobgrindingutilsCacheChickenSwellModel) {
            return original.call(definition);
        }
        ModelPart cached = coo$swellRoot;
        if (cached == null) {
            cached = original.call(definition);
            coo$swellRoot = cached;
        }
        return cached;
    }
}
