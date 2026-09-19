package com.misanthropy.collections_of_optimizations.mixin.emf;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockEntityType.class)
public abstract class MixinBlockEntityTypeName {

    @Unique
    private String coo$name;

    @Override
    public String toString() {
        if (!CoOConfig.emfCacheBlockEntityTypeName) {
            return super.toString();
        }
        String name = this.coo$name;
        if (name == null) {
            name = super.toString();
            this.coo$name = name;
        }
        return name;
    }
}
