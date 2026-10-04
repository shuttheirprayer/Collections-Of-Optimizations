package com.misanthropy.collections_of_optimizations.mixin.cataclysm;

import com.github.L_Ender.cataclysm.entity.etc.Animation_Monsters;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = Animation_Monsters.class, remap = false)
public abstract class MixinCataclysmBossMusic {

    @Unique
    private boolean coo$stopSent;

    @WrapOperation(
            method = "m_8119_",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/network/simple/SimpleChannel;send(Lnet/minecraftforge/network/PacketDistributor$PacketTarget;Ljava/lang/Object;)V",
                    ordinal = 0
            ),
            require = 0
    )
    private void coo$playMusic(SimpleChannel channel, PacketDistributor.PacketTarget target, Object message, Operation<Void> original) {
        this.coo$stopSent = false;
        original.call(channel, target, message);
    }

    @WrapOperation(
            method = "m_8119_",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/network/simple/SimpleChannel;send(Lnet/minecraftforge/network/PacketDistributor$PacketTarget;Ljava/lang/Object;)V",
                    ordinal = 1
            ),
            require = 0
    )
    private void coo$stopMusicOnce(SimpleChannel channel, PacketDistributor.PacketTarget target, Object message, Operation<Void> original) {
        if (CoOConfig.cataclysmBossMusicOnChange && this.coo$stopSent) {
            return;
        }
        this.coo$stopSent = true;
        original.call(channel, target, message);
    }
}
