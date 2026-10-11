package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.core.TagKeyNumber;
import net.minecraft.tags.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(TagKey.class)
public abstract class MixinTagKeyNumber implements TagKeyNumber {

    @Unique
    private int coo$number;

    @Override
    public int coo$tagNumber() {
        return this.coo$number;
    }

    @Override
    public void coo$tagNumber(int number) {
        this.coo$number = number;
    }
}
