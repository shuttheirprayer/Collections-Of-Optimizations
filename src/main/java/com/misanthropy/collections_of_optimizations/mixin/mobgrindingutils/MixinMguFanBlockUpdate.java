package com.misanthropy.collections_of_optimizations.mixin.mobgrindingutils;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "mob_grinding_utils.tile.TileEntityFan", remap = false)
public abstract class MixinMguFanBlockUpdate {

    @Shadow
    public boolean showRenderBox;

    @Shadow
    float xPos;

    @Shadow
    float yPos;

    @Shadow
    float zPos;

    @Shadow
    float xNeg;

    @Shadow
    float yNeg;

    @Shadow
    float zNeg;

    @Unique
    private boolean coo$syncPrimed;

    @Unique
    private boolean coo$lastShowRenderBox;

    @Unique
    private float coo$lastXPos;

    @Unique
    private float coo$lastYPos;

    @Unique
    private float coo$lastZPos;

    @Unique
    private float coo$lastXNeg;

    @Unique
    private float coo$lastYNeg;

    @Unique
    private float coo$lastZNeg;

    @WrapOperation(
            method = "setAABBWithModifiers()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;sendBlockUpdated(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;I)V",
                    remap = true
            ),
            require = 0
    )
    private void coo$skipUnchangedFanBroadcast(Level level, BlockPos pos, BlockState oldState, BlockState newState,
                                               int flags, Operation<Void> original) {
        if (!CoOConfig.mobgrindingutilsLeanFanBroadcast) {
            original.call(level, pos, oldState, newState, flags);
            return;
        }
        if (coo$syncPrimed
                && coo$lastShowRenderBox == showRenderBox
                && coo$lastXPos == xPos
                && coo$lastYPos == yPos
                && coo$lastZPos == zPos
                && coo$lastXNeg == xNeg
                && coo$lastYNeg == yNeg
                && coo$lastZNeg == zNeg) {
            return;
        }
        coo$syncPrimed = true;
        coo$lastShowRenderBox = showRenderBox;
        coo$lastXPos = xPos;
        coo$lastYPos = yPos;
        coo$lastZPos = zPos;
        coo$lastXNeg = xNeg;
        coo$lastYNeg = yNeg;
        coo$lastZNeg = zNeg;
        original.call(level, pos, oldState, newState, flags);
    }
}
