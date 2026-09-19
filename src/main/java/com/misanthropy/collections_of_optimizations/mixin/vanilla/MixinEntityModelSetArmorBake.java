package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ArmorModelBake;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityModelSet.class)
public abstract class MixinEntityModelSetArmorBake {

    @WrapMethod(method = "bakeLayer", require = 0)
    private ModelPart coo$reuseArmorBake(ModelLayerLocation layer, Operation<ModelPart> original) {
        if (!CoOConfig.vanillaCacheArmorModelBakes || !ArmorModelBake.active()) {
            return original.call(layer);
        }
        ModelPart cached = ArmorModelBake.get(layer);
        if (cached != null) {
            return cached;
        }
        ModelPart baked = original.call(layer);
        if (baked != null && ArmorModelBake.get(layer) == null) {
            ArmorModelBake.put(layer, baked);
        }
        return baked;
    }

    @Inject(method = "onResourceManagerReload", at = @At("RETURN"), require = 0)
    private void coo$dropArmorBakes(ResourceManager manager, CallbackInfo ci) {
        ArmorModelBake.clear();
    }
}
