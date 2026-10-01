package com.misanthropy.collections_of_optimizations.mixin.alexscaves;

import com.github.alexmodguy.alexscaves.server.block.poi.ACPOIRegistry;
import com.github.alexmodguy.alexscaves.server.entity.util.MagnetUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Mixin(value = MagnetUtil.class, remap = false)
public abstract class MixinMagnetUtilSingleQuery {

    @WrapOperation(
            method = "tickMagnetism",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/github/alexmodguy/alexscaves/server/entity/util/MagnetUtil;getNearbyAttractingMagnets(Lnet/minecraft/core/BlockPos;Lnet/minecraft/server/level/ServerLevel;I)Ljava/util/stream/Stream;"
            ),
            require = 0
    )
    private static Stream<BlockPos> coo$queryBothMagnetKinds(BlockPos pos, ServerLevel level, int range,
                                                             Operation<Stream<BlockPos>> original,
                                                             @Share("coo$repelling") LocalRef<List<BlockPos>> repelling) {
        if (!CoOConfig.alexscavesSingleMagnetQuery) {
            return original.call(pos, level, range);
        }
        ResourceKey<PoiType> attractKey = ACPOIRegistry.ATTRACTING_MAGNETS.getKey();
        ResourceKey<PoiType> repelKey = ACPOIRegistry.REPELLING_MAGNETS.getKey();
        List<BlockPos> attracting = new ArrayList<>();
        List<BlockPos> repels = new ArrayList<>();
        level.getPoiManager()
                .getInRange(type -> type.is(attractKey) || type.is(repelKey), pos, range, PoiManager.Occupancy.ANY)
                .forEach(record -> coo$sort(record, attractKey, attracting, repels));
        repelling.set(repels);
        return attracting.stream();
    }

    @WrapOperation(
            method = "tickMagnetism",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/github/alexmodguy/alexscaves/server/entity/util/MagnetUtil;getNearbyRepellingMagnets(Lnet/minecraft/core/BlockPos;Lnet/minecraft/server/level/ServerLevel;I)Ljava/util/stream/Stream;"
            ),
            require = 0
    )
    private static Stream<BlockPos> coo$reuseRepellingMagnets(BlockPos pos, ServerLevel level, int range,
                                                              Operation<Stream<BlockPos>> original,
                                                              @Share("coo$repelling") LocalRef<List<BlockPos>> repelling) {
        List<BlockPos> repels = repelling.get();
        return repels != null ? repels.stream() : original.call(pos, level, range);
    }

    private static void coo$sort(PoiRecord record, ResourceKey<PoiType> attractKey,
                                 List<BlockPos> attracting, List<BlockPos> repels) {
        (record.getPoiType().is(attractKey) ? attracting : repels).add(record.getPos());
    }
}
