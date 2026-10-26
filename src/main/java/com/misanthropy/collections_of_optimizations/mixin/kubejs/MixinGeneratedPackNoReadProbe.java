package com.misanthropy.collections_of_optimizations.mixin.kubejs;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.nio.file.Path;
import java.util.function.Predicate;

@Pseudo
@Mixin(targets = "dev.latvian.mods.kubejs.script.data.GeneratedResourcePack", remap = false)
public abstract class MixinGeneratedPackNoReadProbe {

    @Unique
    private static final Predicate<Path> COO$ANY = path -> true;

    @ModifyArg(
            method = "getGenerated",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/stream/Stream;filter(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;",
                    ordinal = 2
            ),
            require = 0
    )
    private Predicate<Path> coo$skipReadProbe(Predicate<Path> readable) {
        return CoOConfig.kubejsSkipFileNameScan ? COO$ANY : readable;
    }
}
