package com.misanthropy.collections_of_optimizations.mixin.witherreincarnated;

import com.alexander.witherreincarnated.capabilities.possessed.PossessedCapability;
import com.alexander.witherreincarnated.capabilities.possessed.PossessedCapabilityProvider;
import com.misanthropy.collections_of_optimizations.core.WrCapCache;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Mob.class)
public abstract class MixinWrMobCapCache implements WrCapCache {

    @Unique
    private PossessedCapability coo$wrCap;

    @Override
    public PossessedCapability coo$wrCap() {
        PossessedCapability cap = this.coo$wrCap;
        if (cap == null) {
            cap = ((Mob) (Object) this).getCapability(PossessedCapabilityProvider.CAP).orElse(null);
            this.coo$wrCap = cap;
        }
        return cap;
    }
}
