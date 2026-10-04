package com.misanthropy.collections_of_optimizations.mixin.lionfishapi;

import com.github.L_Ender.lionfishapi.client.model.tools.AdvancedEntityModel;
import com.github.L_Ender.lionfishapi.client.model.tools.AdvancedModelBox;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = AdvancedEntityModel.class, remap = false)
public abstract class MixinLionfishPartsCache {

    @Unique
    private Iterable<AdvancedModelBox> coo$parts;

    @Unique
    private boolean coo$foreign;

    @Redirect(
            method = "resetToDefaultPose",
            at = @At(value = "INVOKE", target = "Lcom/github/L_Ender/lionfishapi/client/model/tools/AdvancedEntityModel;getAllParts()Ljava/lang/Iterable;"),
            require = 0
    )
    private Iterable<AdvancedModelBox> coo$cachedParts(AdvancedEntityModel<?> model) {
        if (!CoOConfig.lionfishapiCacheModelParts) {
            return model.getAllParts();
        }
        Iterable<AdvancedModelBox> parts = this.coo$parts;
        if (parts != null) {
            return parts;
        }
        parts = model.getAllParts();
        if (!this.coo$foreign) {
            if (model.getClass().getName().startsWith("com.github.L_Ender.cataclysm.")) {
                this.coo$parts = parts;
            } else {
                this.coo$foreign = true;
            }
        }
        return parts;
    }
}
