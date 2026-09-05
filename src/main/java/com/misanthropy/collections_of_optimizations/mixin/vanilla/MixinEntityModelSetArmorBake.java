package com.misanthropy.collections_of_optimizations.mixin.vanilla;

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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityModelSet.class)
public abstract class MixinEntityModelSetArmorBake {

    @Inject(method = "bakeLayer", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$reuseArmorBake(ModelLayerLocation layer, CallbackInfoReturnable<ModelPart> cir) {
        if (!CoOConfig.vanillaCacheArmorModelBakes || !ArmorModelBake.active()) {
            return;
        }
        ModelPart cached = ArmorModelBake.get(layer);
        if (cached != null) {
            cir.setReturnValue(cached);
        }
    }

    @Inject(method = "bakeLayer", at = @At("RETURN"), require = 0)
    private void coo$rememberArmorBake(ModelLayerLocation layer, CallbackInfoReturnable<ModelPart> cir) {
        if (!CoOConfig.vanillaCacheArmorModelBakes || !ArmorModelBake.active()) {
            return;
        }
        ModelPart baked = cir.getReturnValue();
        if (baked != null && ArmorModelBake.get(layer) == null) {
            ArmorModelBake.put(layer, baked);
        }
    }

    @Inject(method = "onResourceManagerReload", at = @At("RETURN"), require = 0)
    private void coo$dropArmorBakes(ResourceManager manager, CallbackInfo ci) {
        ArmorModelBake.clear();
    }
}
