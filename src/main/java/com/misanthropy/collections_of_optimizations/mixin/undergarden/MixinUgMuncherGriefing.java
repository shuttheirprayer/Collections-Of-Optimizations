package com.misanthropy.collections_of_optimizations.mixin.undergarden;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "quek.undergarden.entity.cavern.Muncher", remap = false)
public abstract class MixinUgMuncherGriefing {

    @WrapOperation(
            method = "m_8107_",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;m_46953_(Lnet/minecraft/core/BlockPos;ZLnet/minecraft/world/entity/Entity;)Z"
            ),
            remap = false,
            require = 0
    )
    private boolean coo$respectMobGriefing(Level level, BlockPos pos, boolean drop, Entity breaker, Operation<Boolean> original) {
        if (!CoOConfig.undergardenMuncherRespectMobGriefing || level == null || breaker == null) {
            return original.call(level, pos, drop, breaker);
        }
        if (breaker.horizontalCollision && !ForgeEventFactory.getMobGriefingEvent(level, breaker)) {
            return false;
        }
        return original.call(level, pos, drop, breaker);
    }
}
