package com.misanthropy.collections_of_optimizations.mixin.l2archery;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.L2ArcheryClientCache;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "dev.xkmc.l2archery.content.entity.GenericArrowRenderer", remap = false)
public abstract class MixinL2ArcheryArrowTexture {

    @WrapOperation(
            method = "getTextureLocation(Ldev/xkmc/l2archery/content/entity/GenericArrowEntity;)Lnet/minecraft/resources/ResourceLocation;",
            at = @At(value = "NEW", target = "(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"),
            require = 0
    )
    private ResourceLocation coo$reuseArrowTexture(String namespace, String path, Operation<ResourceLocation> original) {
        if (!CoOConfig.l2archeryCacheArrowTexture) {
            return original.call(namespace, path);
        }

        ResourceLocation cached = L2ArcheryClientCache.texture(namespace, path);
        if (cached != null) {
            return cached;
        }

        ResourceLocation made = original.call(namespace, path);
        L2ArcheryClientCache.putTexture(namespace, made);
        return made;
    }
}
