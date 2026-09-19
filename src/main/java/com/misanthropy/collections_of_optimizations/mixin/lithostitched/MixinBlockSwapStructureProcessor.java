package com.misanthropy.collections_of_optimizations.mixin.lithostitched;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import dev.worldgen.lithostitched.worldgen.processor.BlockSwapStructureProcessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.IdentityHashMap;
import java.util.Map;

@Mixin(value = BlockSwapStructureProcessor.class, remap = false)
public abstract class MixinBlockSwapStructureProcessor {

    @Shadow
    @Final
    private Map<ResourceLocation, ResourceLocation> blockSwapMap;

    @Unique
    private volatile Map<Block, Block> coo$resolvedSwaps;

    @Inject(method = "m_7382_", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$fastBlockSwap(
            LevelReader level,
            BlockPos pos,
            BlockPos pivot,
            StructureTemplate.StructureBlockInfo relative,
            StructureTemplate.StructureBlockInfo current,
            StructurePlaceSettings settings,
            CallbackInfoReturnable<StructureTemplate.StructureBlockInfo> cir) {
        if (!CoOConfig.lithostitchedCacheBlockSwapMap) {
            return;
        }

        Map<Block, Block> resolved = this.coo$resolvedSwaps;
        if (resolved == null) {
            resolved = new IdentityHashMap<>(this.blockSwapMap.size());
            for (Map.Entry<ResourceLocation, ResourceLocation> swap : this.blockSwapMap.entrySet()) {
                if (BuiltInRegistries.BLOCK.containsKey(swap.getKey())) {
                    resolved.put(BuiltInRegistries.BLOCK.get(swap.getKey()), BuiltInRegistries.BLOCK.get(swap.getValue()));
                }
            }
            this.coo$resolvedSwaps = resolved;
        }

        Block replacement = resolved.get(current.state().getBlock());
        cir.setReturnValue(replacement == null
                ? current
                : new StructureTemplate.StructureBlockInfo(current.pos(), replacement.withPropertiesOf(current.state()), current.nbt()));
    }
}
