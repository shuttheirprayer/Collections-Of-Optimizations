package com.misanthropy.collections_of_optimizations.mixin.geckolib;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.NumberSyntax;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import software.bernie.geckolib.core.molang.MolangParser;

@Mixin(value = MolangParser.class, remap = false)
public abstract class MixinMolangParserNumbers {

    @Redirect(
            method = "parseJson",
            at = @At(value = "INVOKE", target = "Ljava/lang/Double;parseDouble(Ljava/lang/String;)D"),
            require = 0
    )
    private static double coo$parseWithoutThrowing(String text) {
        return CoOConfig.geckolibParseMolangNumbersWithoutExceptions ? NumberSyntax.parseDouble(text) : Double.parseDouble(text);
    }
}
