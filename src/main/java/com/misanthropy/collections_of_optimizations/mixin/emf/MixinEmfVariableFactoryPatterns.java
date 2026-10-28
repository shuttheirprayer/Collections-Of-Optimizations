package com.misanthropy.collections_of_optimizations.mixin.emf;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.RegexCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import traben.entity_model_features.models.animation.math.variables.factories.GlobalVariableFactory;
import traben.entity_model_features.models.animation.math.variables.factories.ModelPartVariableFactory;
import traben.entity_model_features.models.animation.math.variables.factories.ModelVariableFactory;
import traben.entity_model_features.models.animation.math.variables.factories.RenderVariableFactory;

@Mixin(value = {ModelPartVariableFactory.class, ModelVariableFactory.class, GlobalVariableFactory.class, RenderVariableFactory.class}, remap = false)
public abstract class MixinEmfVariableFactoryPatterns {

    @Redirect(
            method = "createsThisVariable",
            at = @At(value = "INVOKE", target = "Ljava/lang/String;matches(Ljava/lang/String;)Z"),
            require = 0
    )
    private boolean coo$compiledOnce(String input, String regex) {
        return CoOConfig.emfCompiledPatterns ? RegexCache.matches(input, regex) : input.matches(regex);
    }
}
