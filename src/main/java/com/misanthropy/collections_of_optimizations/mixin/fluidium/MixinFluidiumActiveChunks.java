package com.misanthropy.collections_of_optimizations.mixin.fluidium;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(targets = "me.kall.fluidium.common.data.ActiveChunks", remap = false)
public abstract class MixinFluidiumActiveChunks {

    @Shadow
    @Final
    public static Set<ResourceLocation> UPDATE_REQUIRED;

    @Inject(
            method = "dimChange(Lnet/minecraftforge/event/entity/player/PlayerEvent$PlayerChangedDimensionEvent;)V",
            at = @At("HEAD"),
            require = 0)
    private static void coo$refreshDepartedDimension(PlayerEvent.PlayerChangedDimensionEvent event, CallbackInfo ci) {
        if (!CoOConfig.fluidiumRefreshDepartedDimension) {
            return;
        }
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        MinecraftServer server = player.server;
        ResourceKey<Level> from = event.getFrom();
        if (server == null || from == null) {
            return;
        }
        ResourceLocation id = from.location();
        Set<ResourceLocation> pending = UPDATE_REQUIRED;
        if (pending == null) {
            return;
        }
        server.execute(() -> pending.add(id));
    }
}
