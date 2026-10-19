package com.misanthropy.collections_of_optimizations.mixin.immersiveweathering;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ImmersiveWeatheringSpread;
import com.ordana.immersive_weathering.data.block_growths.growths.ConfigurableBlockGrowth;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(targets = "com.ordana.immersive_weathering.data.block_growths.area_condition.AreaCheck", remap = false)
public abstract class MixinAreaCheck {

    @Shadow @Final private int rX;
    @Shadow @Final private int rY;
    @Shadow @Final private int rZ;
    @Shadow @Final private int requiredAmount;
    @Shadow @Final private Optional<Integer> yOffset;
    @Shadow @Final private Optional<RuleTest> mustHavePredicate;
    @Shadow @Final private Optional<RuleTest> mustNotHavePredicate;
    @Shadow @Final private Optional<HolderSet<Block>> extraIncluded;

    @Inject(method = "test", at = @At("HEAD"), cancellable = true)
    private void coo$scanArea(BlockPos pos, Level level, ConfigurableBlockGrowth config, CallbackInfoReturnable<Boolean> cir) {
        if (CoOConfig.immersiveweatheringAreaScan && ImmersiveWeatheringSpread.fits(this.rX, this.rY, this.rZ)) {
            cir.setReturnValue(ImmersiveWeatheringSpread.area(pos, level, config, this.rX, this.rY, this.rZ, this.requiredAmount,
                    this.yOffset, this.mustHavePredicate, this.mustNotHavePredicate, this.extraIncluded));
        }
    }
}
