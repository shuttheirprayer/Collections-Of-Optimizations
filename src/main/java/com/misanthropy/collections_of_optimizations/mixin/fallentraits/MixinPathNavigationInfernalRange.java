package com.misanthropy.collections_of_optimizations.mixin.fallentraits;

import com.misanthropy.collections_of_optimizations.core.FtInfernalRange;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PathNavigation.class)
public abstract class MixinPathNavigationInfernalRange {

    @Shadow
    @Final
    protected Mob mob;

    @ModifyVariable(
            method = "createPath(Ljava/util/Set;IZIF)Lnet/minecraft/world/level/pathfinder/Path;",
            at = @At("HEAD"),
            argsOnly = true,
            require = 0
    )
    private float coo$clampInfernalFollowRange(float followRange) {
        return followRange > FtInfernalRange.CAP ? (float) FtInfernalRange.clamp(this.mob, followRange) : followRange;
    }
}
