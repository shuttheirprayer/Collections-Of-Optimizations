package com.misanthropy.collections_of_optimizations.mixin.witherreincarnated;

import com.alexander.witherreincarnated.events.HandleWitherMusicEvent;
import com.misanthropy.collections_of_optimizations.core.WrState;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collections;

@Mixin(value = HandleWitherMusicEvent.class, remap = false)
public abstract class MixinWrMusicScan {

    @Redirect(
            method = "tickMusic",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;entitiesForRendering()Ljava/lang/Iterable;", remap = true)
    )
    private static Iterable<Entity> coo$skipScanWithoutWithers(ClientLevel level) {
        return WrState.noClientWithers() ? Collections.emptyList() : level.entitiesForRendering();
    }
}
