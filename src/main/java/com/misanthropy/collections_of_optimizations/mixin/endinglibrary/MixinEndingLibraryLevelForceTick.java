package com.misanthropy.collections_of_optimizations.mixin.endinglibrary;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.sugar.Local;
import com.mega.endinglib.api.capability.EntitySyncCapabilityBase;
import com.mega.endinglib.api.capability.IEntityAutoCap;
import com.mega.endinglib.api.capability.IForceTickEntityCap;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;
import java.util.Collections;

@Mixin(value = Level.class, priority = 1500)
public abstract class MixinEndingLibraryLevelForceTick {

    @TargetHandler(
            mixin = "com.mega.endinglib.mixin.time.time.LevelMixin",
            name = "guardEntityTick"
    )
    @Redirect(
            method = "@MixinSquared:Handler",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mega/endinglib/api/capability/IEntityAutoCap;endinglib$getAutoCaps()Ljava/util/Collection;",
                    ordinal = 1,
                    remap = false
            ),
            require = 0,
            remap = false
    )
    private Collection<LazyOptional<EntitySyncCapabilityBase>> coo$forceTickWithoutLambdas(IEntityAutoCap holder, @Local(argsOnly = true) Entity entity) {
        Collection<LazyOptional<EntitySyncCapabilityBase>> caps = holder.endinglib$getAutoCaps();
        if (!CoOConfig.endinglibraryInlineForceTick) {
            return caps;
        }
        for (LazyOptional<EntitySyncCapabilityBase> cap : caps) {
            if (cap.orElse(null) instanceof IForceTickEntityCap forceTick) {
                forceTick.forceTick(entity);
            }
        }
        return Collections.emptyList();
    }
}
