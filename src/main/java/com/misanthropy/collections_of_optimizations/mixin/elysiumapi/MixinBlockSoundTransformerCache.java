package com.misanthropy.collections_of_optimizations.mixin.elysiumapi;

import com.misanthropy.collections_of_optimizations.core.ElysiumSoundHolder;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Block.class)
public abstract class MixinBlockSoundTransformerCache implements ElysiumSoundHolder {

    @Unique
    private volatile Object coo$elysiumSound;

    @Override
    public Object coo$getElysiumSound() {
        return this.coo$elysiumSound;
    }

    @Override
    public void coo$setElysiumSound(Object entry) {
        this.coo$elysiumSound = entry;
    }
}
