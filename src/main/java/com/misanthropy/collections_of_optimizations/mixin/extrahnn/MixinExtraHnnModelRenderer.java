package com.misanthropy.collections_of_optimizations.mixin.extrahnn;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Pseudo
@Mixin(targets = "net.lmor.extrahnn.client.ExtraDataModelItemStackRenderer", remap = false)
public abstract class MixinExtraHnnModelRenderer {

    @ModifyExpressionValue(
            method = "m_108829_(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/lmor/extrahnn/item/ExtraDataModelItem;getStoredModels(Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;"
            ),
            require = 0
    )
    private List<Object> coo$capRenderedModels(List<Object> original) {
        int cap = CoOConfig.extrahnnRenderedModelCap;
        if (cap <= 0 || original == null || original.size() <= cap) {
            return original;
        }
        return original.subList(0, cap);
    }
}
