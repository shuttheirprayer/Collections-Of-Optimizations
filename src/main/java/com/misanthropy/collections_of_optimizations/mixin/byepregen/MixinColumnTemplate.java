package com.misanthropy.collections_of_optimizations.mixin.byepregen;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moepus.byepregen.dfc.runtime.ColumnTemplate;
import com.moepus.byepregen.dfc.runtime.CompiledColumnEvaluator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ColumnTemplate.class, remap = false)
public abstract class MixinColumnTemplate {

    @Unique
    private volatile boolean coo$unbindable;

    @ModifyReturnValue(method = "available", at = @At("RETURN"))
    private boolean coo$skipUnbindable(boolean available) {
        return available && !coo$unbindable;
    }

    @WrapMethod(method = "bindResolved")
    private CompiledColumnEvaluator coo$rememberUnbindable(ColumnTemplate.BindingResolver resolver,
                                                           Operation<CompiledColumnEvaluator> original) {
        try {
            return original.call(resolver);
        } catch (IllegalStateException e) {
            String message = e.getMessage();
            if (message != null && message.startsWith("Cannot bind an unwrapped density subtree containing Interpolated")) {
                coo$unbindable = true;
            }
            throw e;
        }
    }
}
