package com.misanthropy.collections_of_optimizations.mixin.almostunified;

import com.almostreliable.unified.config.DuplicationConfig;
import com.almostreliable.unified.recipe.RecipeLink;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.AlmostUnifiedDuplicates;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Pseudo
@Mixin(targets = "com.almostreliable.unified.recipe.RecipeTransformer", remap = false)
public abstract class MixinRecipeTransformerDuplicates {

    @Shadow
    @Final
    private DuplicationConfig duplicationConfig;

    @Inject(method = "handleDuplicates", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$compareWithinSameFields(Collection<RecipeLink> recipeLinks, List<RecipeLink> linksToCompare,
                                             CallbackInfoReturnable<Set<RecipeLink.DuplicateLink>> cir) {
        if (!CoOConfig.almostunifiedBucketDuplicates) {
            return;
        }
        Set<RecipeLink.DuplicateLink> duplicates = AlmostUnifiedDuplicates.handle(duplicationConfig, recipeLinks, linksToCompare);
        if (duplicates != null) {
            cir.setReturnValue(duplicates);
        }
    }
}
