package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.OutlineStyleMemo;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Font.class)
public abstract class MixinFontOutlineStyle {

    @Unique
    private OutlineStyleMemo coo$outlineStyle;

    @Inject(method = "m_168645_", at = @At("HEAD"), remap = false, require = 0)
    private void coo$resetOutlineStyle(CallbackInfo ci) {
        this.coo$outlineStyle = null;
    }

    @WrapOperation(
            method = "m_168654_",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/Style;m_178520_(I)Lnet/minecraft/network/chat/Style;"
            ),
            remap = false,
            require = 0
    )
    private Style coo$memoOutlineStyle(Style style, int color, Operation<Style> original) {
        if (!CoOConfig.vanillaMemoOutlineStyle) {
            return original.call(style, color);
        }
        OutlineStyleMemo memo = this.coo$outlineStyle;
        if (memo != null && memo.matches(style, color)) {
            return memo.result();
        }
        Style result = original.call(style, color);
        this.coo$outlineStyle = new OutlineStyleMemo(style, color, result);
        return result;
    }
}
