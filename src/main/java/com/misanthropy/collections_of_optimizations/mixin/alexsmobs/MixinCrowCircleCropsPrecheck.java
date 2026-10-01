package com.misanthropy.collections_of_optimizations.mixin.alexsmobs;

import com.github.alexthe666.alexsmobs.entity.ai.CrowAICircleCrops;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.SectionPrecheck;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MoveToBlockGoal.class)
public abstract class MixinCrowCircleCropsPrecheck {

    @Shadow
    @Final
    protected PathfinderMob mob;

    @Shadow
    @Final
    private int searchRange;

    @Shadow
    @Final
    private int verticalSearchRange;

    @Shadow
    protected int verticalSearchStart;

    @Inject(method = "findNearestBlock", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$skipEmptyCropScan(CallbackInfoReturnable<Boolean> cir) {
        if (!CoOConfig.alexsmobsCrowScanPrecheck || !((Object) this instanceof CrowAICircleCrops)) {
            return;
        }
        BlockPos pos = this.mob.blockPosition();
        int horizontal = this.searchRange - 1;
        int vertical = Math.max(this.verticalSearchRange, Math.abs(this.verticalSearchStart));
        if (!SectionPrecheck.mayContain(this.mob.level(),
                pos.getX() - horizontal, pos.getY() - 1 - vertical, pos.getZ() - horizontal,
                pos.getX() + horizontal, pos.getY() - 1 + vertical, pos.getZ() + horizontal,
                state -> state.is(AMTagRegistry.CROW_FOODBLOCKS))) {
            cir.setReturnValue(false);
        }
    }
}
