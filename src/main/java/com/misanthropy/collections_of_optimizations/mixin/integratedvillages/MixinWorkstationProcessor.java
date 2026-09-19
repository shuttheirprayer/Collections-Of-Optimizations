package com.misanthropy.collections_of_optimizations.mixin.integratedvillages;

import com.craisinlord.integrated_villages.world.processors.WorkstationProcessor;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;

@Mixin(value = WorkstationProcessor.class, remap = false)
public abstract class MixinWorkstationProcessor {

    @Shadow
    private ArrayList<String> outputBlocksString;

    @WrapMethod(method = "m_7382_", require = 0)
    private StructureTemplate.StructureBlockInfo coo$isolatedWorkstationPick(LevelReader level, BlockPos pos, BlockPos pivot,
                                                                            StructureTemplate.StructureBlockInfo local,
                                                                            StructureTemplate.StructureBlockInfo world,
                                                                            StructurePlaceSettings settings,
                                                                            Operation<StructureTemplate.StructureBlockInfo> original) {
        if (!CoOConfig.integratedvillagesIsolateWorkstationPicks) {
            return original.call(level, pos, pivot, local, world, settings);
        }
        synchronized (this) {
            this.outputBlocksString.clear();
            return original.call(level, pos, pivot, local, world, settings);
        }
    }
}
