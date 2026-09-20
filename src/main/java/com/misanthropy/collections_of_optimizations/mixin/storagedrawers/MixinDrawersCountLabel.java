package com.misanthropy.collections_of_optimizations.mixin.storagedrawers;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.DrawersCountLabelCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "com.jaquadro.minecraft.storagedrawers.util.CountFormatter", remap = false)
public abstract class MixinDrawersCountLabel {

    @WrapOperation(
            method = "formatApprox(Lnet/minecraft/client/gui/Font;Lcom/jaquadro/minecraft/storagedrawers/api/storage/IDrawer;)Ljava/lang/String;",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/String;format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"
            ),
            remap = false,
            require = 0
    )
    private static String coo$cacheFormattedCountLabel(String format, Object[] args, Operation<String> original) {
        if (!CoOConfig.storagedrawersCacheCountLabels) {
            return original.call(format, args);
        }

        String cached = DrawersCountLabelCache.lookupFormatted(format, args);
        if (cached != null) {
            return cached;
        }

        String text = original.call(format, args);
        DrawersCountLabelCache.storeFormatted(format, args, text);
        return text;
    }

    @WrapOperation(
            method = "formatApprox(Lnet/minecraft/client/gui/Font;Lcom/jaquadro/minecraft/storagedrawers/api/storage/IDrawer;)Ljava/lang/String;",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/String;valueOf(I)Ljava/lang/String;"
            ),
            remap = false,
            require = 0
    )
    private static String coo$cachePlainCountLabel(int count, Operation<String> original) {
        if (!CoOConfig.storagedrawersCacheCountLabels) {
            return original.call(count);
        }

        String cached = DrawersCountLabelCache.lookupPlain(count);
        if (cached != null) {
            return cached;
        }

        String text = original.call(count);
        DrawersCountLabelCache.storePlain(count, text);
        return text;
    }
}
