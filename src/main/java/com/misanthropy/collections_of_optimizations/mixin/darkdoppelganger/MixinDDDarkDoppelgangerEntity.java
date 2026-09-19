package com.misanthropy.collections_of_optimizations.mixin.darkdoppelganger;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "net.bandit.darkdoppelganger.entity.DarkDoppelgangerEntity", remap = false)
public abstract class MixinDDDarkDoppelgangerEntity {

    @WrapWithCondition(
            method = "summonMinions()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;m_7967_(Lnet/minecraft/world/entity/Entity;)Z",
                    ordinal = 1
            ),
            require = 0
    )
    private boolean coo$skipDuplicateMinionSpawn(Level level, Entity minion) {
        return !CoOConfig.darkdoppelgangerFixDoubleMinionSpawn;
    }
}
