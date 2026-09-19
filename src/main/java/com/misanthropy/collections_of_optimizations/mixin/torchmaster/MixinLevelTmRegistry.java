package com.misanthropy.collections_of_optimizations.mixin.torchmaster;

import com.misanthropy.collections_of_optimizations.core.TorchmasterRegistryHolder;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Level.class)
public abstract class MixinLevelTmRegistry implements TorchmasterRegistryHolder {

    @Unique
    private Object coo$tmLightRegistry;

    @Override
    public Object coo$getTmLightRegistry() {
        return this.coo$tmLightRegistry;
    }

    @Override
    public void coo$setTmLightRegistry(Object value) {
        this.coo$tmLightRegistry = value;
    }
}
