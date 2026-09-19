package com.misanthropy.collections_of_optimizations.mixin.revelationfix;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.RevelationOutlineScan;
import it.unimi.dsi.fastutil.ints.Int2CharOpenHashMap;
import net.minecraft.client.gui.Font;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = Font.class, priority = 1500)
public abstract class MixinFontOutlineScan {

    @TargetHandler(mixin = "com.mega.revelationfix.mixin.gr.FontMixin", name = "drawOutLine")
    @WrapOperation(
            method = "@MixinSquared:Handler",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mega/endinglib/api/client/text/TextColorUtils;getColorChars(Lnet/minecraft/util/FormattedCharSequence;)Lit/unimi/dsi/fastutil/ints/Int2CharOpenHashMap;",
                    remap = false
            ),
            require = 0
    )
    private static Int2CharOpenHashMap coo$scanBeforeMapping(FormattedCharSequence text, Operation<Int2CharOpenHashMap> original) {
        if (CoOConfig.revelationfixSkipOutlineScan && !RevelationOutlineScan.needsOutline(text)) {
            return RevelationOutlineScan.none();
        }
        return original.call(text);
    }
}
