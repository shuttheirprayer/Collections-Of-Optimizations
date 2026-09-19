package com.misanthropy.collections_of_optimizations.mixin.faunify;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.FaunifyPlayerScan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Pseudo
@Mixin(targets = {
        "com.pepper.faunify.entity.MouseEntity$StealSeedGoal",
        "com.pepper.faunify.entity.DragonflyEntity$DartingGoal",
        "com.pepper.faunify.entity.SilkMothEntity$SilkMothFlightGoal",
        "com.pepper.faunify.entity.BeeflyEntity$BeeflyFlightGoal"
}, remap = false)
public abstract class MixinFaunifyPlayerScanGoals {

    @WrapOperation(
            method = "m_8036_",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;m_45976_(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"
            ),
            require = 0
    )
    private List coo$leanPlayerProximityScan(Level level, Class type, AABB box, Operation<List> original) {
        if (!CoOConfig.faunifyLeanPlayerProximityScan || type != Player.class || level == null || box == null) {
            return original.call(level, type, box);
        }
        return FaunifyPlayerScan.playersIn(level, box);
    }
}
