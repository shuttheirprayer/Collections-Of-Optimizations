package com.misanthropy.collections_of_optimizations.mixin.cataclysm;

import com.github.L_Ender.cataclysm.mixin.accessor.StructureManagerAccessor;
import com.github.L_Ender.cataclysm.util.MixinUtil;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MixinUtil.class, remap = false)
public abstract class MixinCataclysmStructureLookup {

    @Inject(method = "getStructureAt", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$noReferencesNoStart(StructureManager structureManager, BlockPos pos, Structure structure,
                                               CallbackInfoReturnable<StructureStart> cir) {
        if (!CoOConfig.cataclysmFastStructureCheck) {
            return;
        }
        if (((StructureManagerAccessor) structureManager).getLevel()
                .getChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()), ChunkStatus.STRUCTURE_REFERENCES)
                .getReferencesForStructure(structure).isEmpty()) {
            cir.setReturnValue(StructureStart.INVALID_START);
        }
    }
}
