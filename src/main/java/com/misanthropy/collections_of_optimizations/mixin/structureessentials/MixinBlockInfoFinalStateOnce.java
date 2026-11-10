package com.misanthropy.collections_of_optimizations.mixin.structureessentials;

import com.misanthropy.collections_of_optimizations.core.FinalStateChecks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StructureTemplate.StructureBlockInfo.class)
public abstract class MixinBlockInfoFinalStateOnce {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void coo$checkFinalState(BlockPos pos, BlockState state, CompoundTag nbt, CallbackInfo ci) {
        if (nbt != null && state.is(Blocks.JIGSAW) && nbt.contains("final_state")) {
            FinalStateChecks.check(nbt.getString("final_state"));
        }
    }
}
