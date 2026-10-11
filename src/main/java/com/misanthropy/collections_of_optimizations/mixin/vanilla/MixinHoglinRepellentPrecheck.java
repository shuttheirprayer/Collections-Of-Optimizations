package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.SectionPrecheck;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.ai.sensing.HoglinSpecificSensor;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HoglinSpecificSensor.class)
public abstract class MixinHoglinRepellentPrecheck {

    @WrapMethod(method = "findNearestRepellent", require = 0)
    private Optional<BlockPos> coo$skipScanWithoutRepellent(ServerLevel level, Hoglin hoglin, Operation<Optional<BlockPos>> original) {
        if (CoOConfig.vanillaPrecheckRepellentScan && !level.isDebug()) {
            BlockPos pos = hoglin.blockPosition();
            if (!SectionPrecheck.mayContain(level,
                    pos.getX() - 8, pos.getY() - 4, pos.getZ() - 8,
                    pos.getX() + 8, pos.getY() + 4, pos.getZ() + 8,
                    state -> state.is(BlockTags.HOGLIN_REPELLENTS))) {
                return Optional.empty();
            }
        }
        return original.call(level, hoglin);
    }
}
