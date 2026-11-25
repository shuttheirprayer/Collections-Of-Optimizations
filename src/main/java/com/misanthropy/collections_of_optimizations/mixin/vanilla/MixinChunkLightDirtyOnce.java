package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPacketListener.class)
public abstract class MixinChunkLightDirtyOnce {

    @Unique
    private boolean coo$chunkLoading;

    @ModifyArg(method = "handleLevelChunkWithLight", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;queueLightUpdate(Ljava/lang/Runnable;)V"), require = 0)
    private Runnable coo$markLoading(Runnable update) {
        return () -> {
            this.coo$chunkLoading = CoOConfig.vanillaMarkLoadedChunkOnce;
            try {
                update.run();
            } finally {
                this.coo$chunkLoading = false;
            }
        };
    }

    @Redirect(method = "readSectionList", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;setSectionDirtyWithNeighbors(III)V"), require = 0)
    private void coo$wholeChunkFollows(ClientLevel level, int x, int y, int z) {
        if (!this.coo$chunkLoading) {
            level.setSectionDirtyWithNeighbors(x, y, z);
        }
    }

    @Redirect(method = "enableChunkLight", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;setSectionDirtyWithNeighbors(III)V"), require = 0)
    private void coo$oneLayerAtATime(ClientLevel level, int x, int y, int z) {
        if (!CoOConfig.vanillaMarkLoadedChunkOnce) {
            level.setSectionDirtyWithNeighbors(x, y, z);
            return;
        }
        LevelRenderer renderer = Minecraft.getInstance().levelRenderer;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                renderer.setSectionDirty(x + dx, y, z + dz);
            }
        }
    }
}
