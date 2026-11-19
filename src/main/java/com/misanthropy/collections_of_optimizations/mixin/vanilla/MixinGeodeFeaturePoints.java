package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.core.GeodePoints;
import net.minecraft.world.level.levelgen.feature.GeodeFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.LinkedList;

@Mixin(GeodeFeature.class)
public abstract class MixinGeodeFeaturePoints {

    @Redirect(method = "place", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Lists;newLinkedList()Ljava/util/LinkedList;", remap = false), require = 0)
    private <E> LinkedList<E> coo$reusableIteratorList() {
        return new GeodePoints<>();
    }
}
