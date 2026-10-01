package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.DensityGraphReuse;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.RandomState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(NoiseChunk.class)
public abstract class MixinNoiseChunkDensityReuse {

    @Unique
    private DensityGraphReuse.FirstPass coo$firstPass;

    @WrapOperation(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/levelgen/NoiseRouter;mapAll(Lnet/minecraft/world/level/levelgen/DensityFunction$Visitor;)Lnet/minecraft/world/level/levelgen/NoiseRouter;"
            )
    )
    private NoiseRouter coo$mapRouterOnce(NoiseRouter router, DensityFunction.Visitor visitor, Operation<NoiseRouter> original,
                                          @Local(argsOnly = true) RandomState randomState) {
        if (!CoOConfig.vanillaReuseNoiseChunkDensityGraph) {
            return original.call(router, visitor);
        }
        DensityGraphReuse.FirstPass pass = DensityGraphReuse.firstPass(randomState, visitor);
        if (pass == null) {
            return original.call(router, visitor);
        }
        this.coo$firstPass = pass;
        return original.call(router, pass);
    }

    @WrapOperation(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/levelgen/DensityFunction;mapAll(Lnet/minecraft/world/level/levelgen/DensityFunction$Visitor;)Lnet/minecraft/world/level/levelgen/DensityFunction;"
            )
    )
    private DensityFunction coo$reuseFinalDensity(DensityFunction function, DensityFunction.Visitor visitor,
                                                  Operation<DensityFunction> original) {
        DensityGraphReuse.FirstPass pass = this.coo$firstPass;
        this.coo$firstPass = null;
        if (pass != null) {
            DensityFunction reused = pass.secondPass(function, visitor);
            if (reused != null) {
                return reused;
            }
        }
        return original.call(function, visitor);
    }
}
