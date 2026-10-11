package com.misanthropy.collections_of_optimizations.mixin.modernfix;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.InsertionOrderedLinkedSet;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import org.embeddedt.modernfix.forge.dynresources.ModelBakeEventHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ModelBakeEventHelper.class, remap = false)
public abstract class MixinModelBakeLocationOrder {

    @WrapOperation(
            method = "<init>",
            at = @At(
                    value = "NEW",
                    target = "(I)Lit/unimi/dsi/fastutil/objects/ObjectLinkedOpenHashSet;"
            ),
            require = 0
    )
    private ObjectLinkedOpenHashSet<Object> coo$walkLocationsInOrder(int expected,
                                                                     Operation<ObjectLinkedOpenHashSet<Object>> original) {
        if (!CoOConfig.modernfixOrderedModelLocations) {
            return original.call(expected);
        }
        return new InsertionOrderedLinkedSet<>(expected);
    }
}
