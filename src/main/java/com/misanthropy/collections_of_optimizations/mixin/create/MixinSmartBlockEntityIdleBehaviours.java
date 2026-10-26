package com.misanthropy.collections_of_optimizations.mixin.create;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.CreateBehaviourTicks;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Consumer;

@Mixin(targets = "com.simibubi.create.foundation.blockEntity.SmartBlockEntity", remap = false)
public abstract class MixinSmartBlockEntityIdleBehaviours {

    @ModifyArg(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/foundation/blockEntity/SmartBlockEntity;forEachBehaviour(Ljava/util/function/Consumer;)V"
            ),
            require = 0
    )
    private Consumer<BlockEntityBehaviour> coo$tickActiveBehaviours(Consumer<BlockEntityBehaviour> action) {
        return CoOConfig.createSkipIdleBehaviourTicks ? CreateBehaviourTicks.TICK_ACTIVE : action;
    }
}
