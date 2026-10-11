package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.SectionPrecheck;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.sensing.PiglinSpecificSensor;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PiglinSpecificSensor.class)
public abstract class MixinPiglinRepellentPrecheck {

    @WrapMethod(method = "findNearestRepellent", require = 0)
    private static Optional<BlockPos> coo$skipScanWithoutRepellent(ServerLevel level, LivingEntity entity, Operation<Optional<BlockPos>> original) {
        if (CoOConfig.vanillaPrecheckRepellentScan && !level.isDebug()) {
            BlockPos pos = entity.blockPosition();
            if (!SectionPrecheck.mayContain(level,
                    pos.getX() - 8, pos.getY() - 4, pos.getZ() - 8,
                    pos.getX() + 8, pos.getY() + 4, pos.getZ() + 8,
                    state -> state.is(BlockTags.PIGLIN_REPELLENTS))) {
                return Optional.empty();
            }
        }
        return original.call(level, entity);
    }
}
