package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraftforge.internal.TextComponentMessageFormatHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = TextComponentMessageFormatHandler.class, remap = false)
public abstract class MixinTextComponentPlainFormat {

    @WrapOperation(
            method = "handle",
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/common/ForgeI18n;parseFormat(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"),
            require = 0
    )
    private static String coo$skipPlainFormat(String format, Object[] args, Operation<String> original) {
        if (CoOConfig.forgePlainTranslationFormat
                && format.indexOf('{') < 0 && format.indexOf('}') < 0 && format.indexOf('\'') < 0) {
            return format;
        }
        return original.call(format, args);
    }
}
