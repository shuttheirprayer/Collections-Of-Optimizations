package com.misanthropy.collections_of_optimizations.mixin.storagedrawers;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(targets = "com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers", remap = false)
public abstract class MixinDrawersCountSyncRadius {

    @ModifyArg(
            method = "syncClientCount(II)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/texelsaurus/minecraft/chameleon/service/ChameleonNetworking;sendToPlayersNear(Lcom/texelsaurus/minecraft/chameleon/network/ChameleonPacket;Lnet/minecraft/server/level/ServerLevel;DDDD)V"
            ),
            index = 5,
            remap = false,
            require = 0
    )
    private double coo$narrowCountSyncRadius(double radius) {
        if (!CoOConfig.storagedrawersNarrowCountSyncRadius) {
            return radius;
        }

        Level level;
        try {
            level = ((BlockEntity) (Object) this).getLevel();
        } catch (Throwable ignored) {
            return radius;
        }
        if (!(level instanceof ServerLevel serverLevel)) {
            return radius;
        }

        MinecraftServer server = serverLevel.getServer();
        if (server == null) {
            return radius;
        }

        int chunks = server.getPlayerList().getViewDistance();
        if (chunks <= 0) {
            return radius;
        }

        double limit = (chunks + 2) * 24.0;
        return limit < radius ? limit : radius;
    }
}
