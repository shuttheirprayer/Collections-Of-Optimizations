package com.misanthropy.collections_of_optimizations.mixin.fallentraits;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.FtDevourerIndex;
import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MobTraitCap.class, remap = false)
public abstract class MixinMobTraitCapDevourerIndex {

    @Inject(method = "tick", at = @At("TAIL"), require = 0)
    private void coo$indexDevourer(LivingEntity mob, CallbackInfo ci) {
        if (CoOConfig.fallentraitsIndexDevourers) {
            FtDevourerIndex.seen(mob, (MobTraitCap) (Object) this);
        }
    }
}
