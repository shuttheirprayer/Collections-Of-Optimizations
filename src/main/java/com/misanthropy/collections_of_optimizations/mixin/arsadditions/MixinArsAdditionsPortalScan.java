package com.misanthropy.collections_of_optimizations.mixin.arsadditions;

import com.github.jarva.arsadditions.event.ModEvents;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ArsAdditionsPortalScanCache;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ModEvents.ServerForgeEvents.class, remap = false)
public abstract class MixinArsAdditionsPortalScan {

    @WrapOperation(
            method = "isPlayerInStructure",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/StructureManager;m_220491_(Lnet/minecraft/core/BlockPos;Lnet/minecraft/tags/TagKey;)Lnet/minecraft/world/level/levelgen/structure/StructureStart;"
            ),
            require = 0)
    private static StructureStart coo$cacheRuinedPortalMiss(StructureManager manager, BlockPos pos, TagKey<Structure> tag,
                                                            Operation<StructureStart> original) {
        if (!CoOConfig.arsadditionsCacheRuinedPortalScan) {
            return original.call(manager, pos, tag);
        }
        StructureStart cached = ArsAdditionsPortalScanCache.cachedMiss(manager, pos);
        if (cached != null) {
            return cached;
        }
        StructureStart found = original.call(manager, pos, tag);
        if (found == null || !found.isValid()) {
            ArsAdditionsPortalScanCache.storeMiss(manager, pos);
        }
        return found;
    }
}
