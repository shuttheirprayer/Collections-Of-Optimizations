package com.misanthropy.collections_of_optimizations.mixin.emf;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.utils.EMFResourceCaching;

import java.util.Optional;

@Mixin(value = EMFResourceCaching.class, remap = false)
public abstract class MixinEMFResourceCaching {

    @Unique
    private static volatile boolean coo$scanFailed;

    @Inject(method = "clearCache", at = @At("HEAD"), require = 0)
    private static void coo$resetScan(CallbackInfo ci) {
        coo$scanFailed = false;
    }

    @Inject(
            method = "scanPrefix",
            at = @At(value = "INVOKE", target = "Ltraben/entity_model_features/EMFException;recordException(Ljava/lang/Exception;)Ljava/lang/Exception;"),
            require = 0
    )
    private static void coo$markScanFailed(CallbackInfo ci) {
        coo$scanFailed = true;
    }

    @WrapOperation(
            method = "resourceExists",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/packs/resources/ResourceManager;getResource(Lnet/minecraft/resources/ResourceLocation;)Ljava/util/Optional;",
                    remap = true
            ),
            require = 0
    )
    private static Optional<Resource> coo$answerFromIndex(ResourceManager manager, ResourceLocation loc, Operation<Optional<Resource>> original) {
        if (CoOConfig.emfIndexedResourceExists && !coo$scanFailed) {
            String path = loc.getPath();
            if ((path.startsWith("emf/cem/") || path.startsWith("optifine/cem/"))
                    && !EMFResourceCaching.RESOURCE_EXISTENCE_CACHE.containsKey(loc.toString())) {
                return Optional.empty();
            }
        }
        return original.call(manager, loc);
    }
}
