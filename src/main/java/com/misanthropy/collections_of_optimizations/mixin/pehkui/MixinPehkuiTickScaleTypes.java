package com.misanthropy.collections_of_optimizations.mixin.pehkui;

import com.bawnorton.mixinsquared.TargetHandler;
import com.google.common.collect.BiMap;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.PehkuiTypeSnapshot;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Set;

@Mixin(value = Entity.class, priority = 1500)
public abstract class MixinPehkuiTickScaleTypes {

    @SuppressWarnings("rawtypes")
    @TargetHandler(
            mixin = "virtuoel.pehkui.mixin.EntityMixin",
            name = "pehkui$tick"
    )
    @Redirect(
            method = "@MixinSquared:Handler",
            at = @At(value = "INVOKE", target = "Lcom/google/common/collect/BiMap;values()Ljava/util/Set;"),
            require = 0,
            remap = false
    )
    private Set coo$snapshotScaleTypes(BiMap map) {
        return CoOConfig.pehkuiSnapshotTickTypes ? PehkuiTypeSnapshot.values(map) : map.values();
    }
}
