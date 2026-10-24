package com.misanthropy.collections_of_optimizations.mixin.elysiumapi;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ClimateSampleMemo;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Climate.Sampler.class)
public abstract class MixinClimateSampler {

    @Shadow @Final private DensityFunction temperature;
    @Shadow @Final private DensityFunction humidity;
    @Shadow @Final private DensityFunction continentalness;
    @Shadow @Final private DensityFunction erosion;
    @Shadow @Final private DensityFunction depth;
    @Shadow @Final private DensityFunction weirdness;

    @Overwrite
    public Climate.TargetPoint sample(int x, int y, int z) {
        ClimateSampleMemo memo = CoOConfig.elysiumapiMemoClimateSample || CoOConfig.alexscavesMemoClimateSample ? ClimateSampleMemo.get() : null;
        if (memo != null) {
            Climate.TargetPoint hit = memo.lookup(this, x, y, z);
            if (hit != null) {
                return hit;
            }
        }
        DensityFunction.SinglePointContext context = new DensityFunction.SinglePointContext(QuartPos.toBlock(x), QuartPos.toBlock(y), QuartPos.toBlock(z));
        Climate.TargetPoint value = Climate.target((float) this.temperature.compute(context), (float) this.humidity.compute(context),
                (float) this.continentalness.compute(context), (float) this.erosion.compute(context), (float) this.depth.compute(context),
                (float) this.weirdness.compute(context));
        if (memo != null) {
            memo.store(this, x, y, z, value);
        }
        return value;
    }
}
