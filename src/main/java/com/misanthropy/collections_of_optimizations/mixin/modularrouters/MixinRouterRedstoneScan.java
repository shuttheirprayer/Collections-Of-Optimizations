package com.misanthropy.collections_of_optimizations.mixin.modularrouters;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ModularRoutersSides;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "me.desht.modularrouters.block.tile.ModularRouterBlockEntity", remap = false)
public abstract class MixinRouterRedstoneScan {

    @Shadow
    private CompoundTag extData;

    @Shadow
    public abstract Level nonNullLevel();

    @Inject(
            method = "calculateIncomingRedstonePower",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void coo$fastIncomingRedstonePower(BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        if (!CoOConfig.modularroutersFastExtruderSideCheck) {
            return;
        }

        CompoundTag ext = this.extData;
        if (ext != null && !ext.isEmpty()) {
            return;
        }

        Level level;
        try {
            level = nonNullLevel();
        } catch (Throwable ignored) {
            return;
        }
        if (level == null) {
            return;
        }

        int power = 0;
        for (Direction facing : ModularRoutersSides.SIDES) {
            int p = level.getSignal(pos.relative(facing), facing);
            if (p >= 15) {
                cir.setReturnValue(p);
                return;
            }
            if (p > power) {
                power = p;
            }
        }
        cir.setReturnValue(power);
    }
}
