package com.misanthropy.collections_of_optimizations.mixin.structurify;

import com.faboslav.structurify.common.api.StructurifyChunkGenerator;
import com.faboslav.structurify.common.api.StructurifyStructure;
import com.faboslav.structurify.common.world.level.structure.checks.StructureChecker;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.StructurifyConfigMirror;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = StructureChecker.class, remap = false)
public abstract class MixinStructureChecker {

    @Inject(
            method = "checkStructure(Lnet/minecraft/world/level/levelgen/structure/StructureStart;Lnet/minecraft/resources/ResourceLocation;Lcom/faboslav/structurify/common/api/StructurifyStructure;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/world/level/LevelHeightAccessor;Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/biome/BiomeSource;)Z",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$skipWhenNoChecksEnabled(StructureStart structureStart,
                                                    ResourceLocation structureId,
                                                    StructurifyStructure structure,
                                                    ChunkGenerator chunkGenerator,
                                                    LevelHeightAccessor heightAccessor,
                                                    RandomState randomState,
                                                    BiomeSource biomeSource,
                                                    CallbackInfoReturnable<Boolean> cir) {
        if (structureStart.getPieces().isEmpty()) {
            return;
        }
        if (CoOConfig.structurifySkipDisabledStructureChecks
                && StructurifyConfigMirror.allStructureChecksDisabled()
                && chunkGenerator instanceof StructurifyChunkGenerator generator
                && generator.structurify$getStructureChecks().isEmpty()) {
            cir.setReturnValue(Boolean.TRUE);
            return;
        }

        StructurifyConfigMirror.markStructureChecksLive();
    }
}
