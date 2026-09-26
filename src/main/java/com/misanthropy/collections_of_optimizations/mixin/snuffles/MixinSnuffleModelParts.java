package com.misanthropy.collections_of_optimizations.mixin.snuffles;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "mod.schnappdragon.snuffles.client.model.SnuffleModel", remap = false)
public abstract class MixinSnuffleModelParts {

    @Unique
    private Iterable<ModelPart> coo$bodyParts;

    @Inject(method = "m_5608_", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$reuseBodyParts(CallbackInfoReturnable<Iterable<ModelPart>> cir) {
        Iterable<ModelPart> cached = this.coo$bodyParts;
        if (cached != null && CoOConfig.snufflesCacheModelParts) {
            cir.setReturnValue(cached);
        }
    }

    @Inject(method = "m_5608_", at = @At("RETURN"), require = 0)
    private void coo$keepBodyParts(CallbackInfoReturnable<Iterable<ModelPart>> cir) {
        if (this.coo$bodyParts == null && CoOConfig.snufflesCacheModelParts) {
            this.coo$bodyParts = cir.getReturnValue();
        }
    }
}
