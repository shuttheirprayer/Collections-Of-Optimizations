package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.NoiseColumnCells;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.RandomState;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Mixin(NoiseBasedChunkGenerator.class)
public abstract class MixinNoiseColumnCellReuse {
    @Shadow
    @Final
    private Holder<NoiseGeneratorSettings> settings;

    @Shadow
    @Final
    private Supplier<Aquifer.FluidPicker> globalFluidPicker;

    @Inject(method = "iterateNoiseColumn", at = @At("HEAD"), cancellable = true)
    private void coo$reuseColumnCell(LevelHeightAccessor level, RandomState randomState, int x, int z,
                                     MutableObject<NoiseColumn> column, Predicate<BlockState> predicate,
                                     CallbackInfoReturnable<OptionalInt> cir) {
        if (!CoOConfig.vanillaReuseNoiseColumnCells || column != null || predicate == null
                || ((Object) this).getClass() != NoiseBasedChunkGenerator.class) {
            return;
        }
        NoiseGeneratorSettings generatorSettings = this.settings.value();
        NoiseSettings noiseSettings = generatorSettings.noiseSettings().clampToHeightAccessor(level);
        int cellHeight = noiseSettings.getCellHeight();
        int minCellY = Mth.floorDiv(noiseSettings.minY(), cellHeight);
        int cellCountY = Mth.floorDiv(noiseSettings.height(), cellHeight);
        if (cellCountY <= 0) {
            cir.setReturnValue(OptionalInt.empty());
            return;
        }
        int cellWidth = noiseSettings.getCellWidth();
        int startX = Math.floorDiv(x, cellWidth) * cellWidth;
        int startZ = Math.floorDiv(z, cellWidth) * cellWidth;
        double fracX = (double) Math.floorMod(x, cellWidth) / (double) cellWidth;
        double fracZ = (double) Math.floorMod(z, cellWidth) / (double) cellWidth;
        NoiseChunk noiseChunk = NoiseColumnCells.get(this, randomState, noiseSettings, startX, startZ, generatorSettings, this.globalFluidPicker.get());
        BlockState defaultBlock = generatorSettings.defaultBlock();
        for (int cellY = cellCountY - 1; cellY >= 0; cellY--) {
            noiseChunk.selectCellYZ(cellY, 0);
            for (int inCellY = cellHeight - 1; inCellY >= 0; inCellY--) {
                int y = (minCellY + cellY) * cellHeight + inCellY;
                noiseChunk.updateForY(y, (double) inCellY / (double) cellHeight);
                noiseChunk.updateForX(x, fracX);
                noiseChunk.updateForZ(z, fracZ);
                BlockState state = noiseChunk.getInterpolatedState();
                if (predicate.test(state == null ? defaultBlock : state)) {
                    cir.setReturnValue(OptionalInt.of(y + 1));
                    return;
                }
            }
        }
        cir.setReturnValue(OptionalInt.empty());
    }
}
