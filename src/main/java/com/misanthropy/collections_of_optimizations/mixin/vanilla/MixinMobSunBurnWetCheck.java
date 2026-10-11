package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.VanillaOverrideChecks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Mob.class)
public abstract class MixinMobSunBurnWetCheck {

    @WrapOperation(
            method = "isSunBurnTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;isInWaterRainOrBubble()Z"),
            require = 0
    )
    private boolean coo$deferWetCheck(Mob mob, Operation<Boolean> original, @Share("coo$wetDeferred") LocalBooleanRef deferred) {
        if (CoOConfig.vanillaDeferSunBurnWetCheck && VanillaOverrideChecks.stockWetCheck(mob.getClass())) {
            deferred.set(true);
            return false;
        }
        return original.call(mob);
    }

    @WrapOperation(
            method = "isSunBurnTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;canSeeSky(Lnet/minecraft/core/BlockPos;)Z"),
            require = 0
    )
    private boolean coo$wetBeforeSky(Level level, BlockPos pos, Operation<Boolean> original, @Share("coo$wetDeferred") LocalBooleanRef deferred) {
        if (deferred.get() && ((Entity) (Object) this).isInWaterRainOrBubble()) {
            return false;
        }
        return original.call(level, pos);
    }
}
