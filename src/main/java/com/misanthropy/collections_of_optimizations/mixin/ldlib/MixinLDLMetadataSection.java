package com.misanthropy.collections_of_optimizations.mixin.ldlib;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.lowdragmc.lowdraglib.client.model.custommodel.LDLMetadataSection;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(value = LDLMetadataSection.class, remap = false)
public abstract class MixinLDLMetadataSection {

    @WrapOperation(
            method = "getMetadata(Lnet/minecraft/resources/ResourceLocation;)Lcom/lowdragmc/lowdraglib/client/model/custommodel/LDLMetadataSection;",
            at = @At(value = "INVOKE", target = "Ljava/util/Optional;get()Ljava/lang/Object;", ordinal = 1),
            require = 0
    )
    private static Object coo$missingSectionWithoutThrowing(Optional<?> section, Operation<Object> original) {
        if (CoOConfig.ldlibSkipMissingMetadataThrow && !section.isPresent()) {
            return LDLMetadataSection.MISSING;
        }
        return original.call(section);
    }
}
