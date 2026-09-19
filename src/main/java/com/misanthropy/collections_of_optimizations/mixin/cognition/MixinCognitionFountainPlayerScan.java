package com.misanthropy.collections_of_optimizations.mixin.cognition;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.CognitionPlayerScan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Pseudo
@Mixin(targets = "com.cyanogen.experienceobelisk.block_entities.ExperienceFountainEntity", remap = false)
public abstract class MixinCognitionFountainPlayerScan {

    @WrapOperation(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;m_45976_(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"
            ),
            require = 0
    )
    private static List coo$leanFountainPlayerScan(Level level, Class type, AABB box, Operation<List> original) {
        if (!CoOConfig.cognitionLeanPlayerScan || type != Player.class || level == null || box == null) {
            return original.call(level, type, box);
        }
        return CognitionPlayerScan.playersIn(level, box);
    }
}
