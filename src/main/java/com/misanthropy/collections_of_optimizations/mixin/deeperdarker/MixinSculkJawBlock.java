package com.misanthropy.collections_of_optimizations.mixin.deeperdarker;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.DeeperDarkerRefs;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.kyanite.deeperdarker.content.blocks.SculkJawBlock", remap = false)
public abstract class MixinSculkJawBlock {

    @Inject(
            method = "m_7892_(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;)V",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void coo$guardInsideBite(BlockState state, Level level, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (!DeeperDarkerRefs.jawRefsUsable()) {
            return;
        }
        if (CoOConfig.deeperdarkerFixSculkJawRepeatBite) {
            BooleanProperty biting = DeeperDarkerRefs.jawBiting();
            if (state.hasProperty(biting) && state.getValue(biting)) {
                ci.cancel();
                return;
            }
        }
        if (!CoOConfig.deeperdarkerFixSculkJawGuards) {
            return;
        }
        BooleanProperty canBite = DeeperDarkerRefs.jawCanBite();
        if (state.hasProperty(canBite) && !state.getValue(canBite)) {
            ci.cancel();
            return;
        }
        if (entity instanceof Player player && (player.isCreative() || player.isCrouching())) {
            ci.cancel();
            return;
        }
        if (entity instanceof Monster monster && monster.getMobType() == DeeperDarkerRefs.sculkMobType()) {
            ci.cancel();
        }
    }
}
