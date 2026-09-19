package com.misanthropy.collections_of_optimizations.mixin.legendarymonsters;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.LmBossMusicState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "net.miauczel.legendary_monsters.entity.AnimatedMonster.OriginClasses.IAnimatedBoss", remap = false)
public abstract class MixinLegendaryMonstersBossMusic {

    @Unique
    private int coo$lastMusicState = -2;

    @Unique
    private int coo$lastMusicTick = Integer.MIN_VALUE;

    @WrapWithCondition(
            method = "m_8119_",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/miauczel/legendary_monsters/LegendaryMonsters;sendMSGToAll(Ljava/lang/Object;)V"
            ),
            require = 0
    )
    private boolean coo$throttleBossMusicBroadcast(Object message) {
        int interval = CoOConfig.legendarymonstersBossMusicInterval;
        if (interval <= 1) {
            return true;
        }
        int state = LmBossMusicState.stateOf(message);
        int now = ((Entity) (Object) this).tickCount;
        int elapsed = now - this.coo$lastMusicTick;
        if (elapsed >= interval || elapsed < 0 || (state >= 0 && state != this.coo$lastMusicState)) {
            this.coo$lastMusicState = state;
            this.coo$lastMusicTick = now;
            return true;
        }
        return false;
    }
}
