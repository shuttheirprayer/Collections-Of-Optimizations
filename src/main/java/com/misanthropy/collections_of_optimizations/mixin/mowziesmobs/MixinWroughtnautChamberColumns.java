package com.misanthropy.collections_of_optimizations.mixin.mowziesmobs;

import com.bobmowzie.mowziesmobs.server.world.feature.structure.WroughtnautChamberStructure;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ScopedColumnMemo;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = WroughtnautChamberStructure.class, remap = false)
public abstract class MixinWroughtnautChamberColumns {

    @WrapMethod(method = "tryWroughtChamber", require = 0)
    private static Pair<BlockPos, Rotation> coo$scopeColumns(ChunkGenerator generator, LevelHeightAccessor heights, int x, int surfaceY, int z, RandomState random, Operation<Pair<BlockPos, Rotation>> original) {
        if (!CoOConfig.mowzieShareChamberColumns) {
            return original.call(generator, heights, x, surfaceY, z, random);
        }
        ScopedColumnMemo memo = ScopedColumnMemo.open();
        try {
            return original.call(generator, heights, x, surfaceY, z, random);
        } finally {
            memo.close();
        }
    }

    @Redirect(method = "tryWroughtChamber", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/ChunkGenerator;getBaseColumn(IILnet/minecraft/world/level/LevelHeightAccessor;Lnet/minecraft/world/level/levelgen/RandomState;)Lnet/minecraft/world/level/NoiseColumn;", remap = true), require = 0)
    private static NoiseColumn coo$sharedColumn(ChunkGenerator generator, int x, int z, LevelHeightAccessor heights, RandomState random) {
        return ScopedColumnMemo.column(generator, x, z, heights, random);
    }
}
