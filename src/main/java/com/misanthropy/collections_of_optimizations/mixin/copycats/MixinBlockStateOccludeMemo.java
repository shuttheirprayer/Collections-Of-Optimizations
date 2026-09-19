package com.misanthropy.collections_of_optimizations.mixin.copycats;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = BlockBehaviour.BlockStateBase.class, priority = 1500)
public abstract class MixinBlockStateOccludeMemo {

    @Unique
    private byte coo$occludeMemo;

    @WrapMethod(method = "canOcclude", require = 0)
    private boolean coo$memoOcclusion(Operation<Boolean> original) {
        if (!CoOConfig.copycatsMemoStateOcclusion) {
            return original.call();
        }
        byte memo = this.coo$occludeMemo;
        if (memo != 0) {
            return memo == 1;
        }
        boolean value = original.call();
        this.coo$occludeMemo = (byte) (value ? 1 : 2);
        return value;
    }
}
