package com.misanthropy.collections_of_optimizations.mixin.radium;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.RadiumTouchableMemo;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(targets = "me.jellysquid.mods.lithium.common.block.BlockStateFlags$5", remap = false)
public abstract class MixinRadiumEntityTouchableFlag {

    @WrapMethod(method = "test(Lnet/minecraft/world/level/block/state/BlockState;)Z", require = 0)
    private boolean coo$touchableByBlockClass(BlockState state, Operation<Boolean> original) {
        if (!CoOConfig.radiumMemoEntityTouchableFlag) {
            return original.call(state);
        }
        RadiumTouchableMemo.Slot slot = RadiumTouchableMemo.slot(state.getBlock().getClass());
        if (slot.known()) {
            return slot.value();
        }
        boolean touchable = original.call(state);
        slot.store(touchable);
        return touchable;
    }
}
