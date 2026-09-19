package com.misanthropy.collections_of_optimizations.mixin.textanimator;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.TextAnimatorTagFilter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import snownee.textanimator.effect.Effect;
import snownee.textanimator.effect.EffectFactory;
import snownee.textanimator.effect.params.Params;

@Mixin(value = EffectFactory.class, remap = false)
public abstract class MixinTextAnimatorEffectFactory {

    @Inject(
            method = "create(Ljava/lang/String;Lsnownee/textanimator/effect/params/Params;)Lsnownee/textanimator/effect/Effect;",
            at = @At("HEAD"),
            require = 0
    )
    private static void coo$stacklessUnknownEffect(String type, Params params, CallbackInfoReturnable<Effect> cir) {
        if (!CoOConfig.textanimatorStacklessEffectErrors) {
            return;
        }
        if (TextAnimatorTagFilter.isKnownType(type)) {
            return;
        }
        throw TextAnimatorTagFilter.UNKNOWN_EFFECT_TYPE;
    }
}
