package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.core.ReusableListIterator;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Iterator;
import java.util.List;

@Mixin(OreFeature.class)
public abstract class MixinOreFeatureTargets {
    @Redirect(method = "doPlace", at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"), require = 0)
    private Iterator<?> coo$reuseTargetIterator(List<?> targets) {
        return ReusableListIterator.of(targets);
    }
}
