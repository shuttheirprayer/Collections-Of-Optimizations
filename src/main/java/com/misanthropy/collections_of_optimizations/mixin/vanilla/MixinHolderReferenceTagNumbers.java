package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.TagKeyNumber;
import com.misanthropy.collections_of_optimizations.core.TagNumberSet;
import net.minecraft.core.Holder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Set;

@Mixin(Holder.Reference.class)
public abstract class MixinHolderReferenceTagNumbers {

    @Unique
    private TagNumberSet coo$tagNumbers;

    @Redirect(
            method = "is(Lnet/minecraft/tags/TagKey;)Z",
            at = @At(value = "INVOKE", target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z"),
            require = 0
    )
    private boolean coo$numberedContains(Set<?> tags, Object tag) {
        if (!CoOConfig.vanillaNumberTagChecks || !(tag instanceof TagKeyNumber key)) {
            return tags.contains(tag);
        }
        TagNumberSet numbers = this.coo$tagNumbers;
        if (numbers == null || numbers.source != tags) {
            numbers = TagNumberSet.of(tags);
            if (numbers == null) {
                return tags.contains(tag);
            }
            this.coo$tagNumbers = numbers;
        }
        return numbers.has(TagNumberSet.number(key));
    }
}
