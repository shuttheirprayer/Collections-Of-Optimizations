package com.misanthropy.collections_of_optimizations.mixin.xaeroworldmap;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.map.core.XaeroWorldMapCore;

@Mixin(value = XaeroWorldMapCore.class, remap = false)
public abstract class MixinXaeroWorldMapCoreDirtyChunks {

    @Inject(method = "chunkUpdateCallback", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$markOnlyThisChunk(int chunkX, int chunkZ, CallbackInfo ci) {
        if (!CoOConfig.xaeroworldmapMarkOnlyChangedChunk) {
            return;
        }
        ci.cancel();
        XaeroWorldMapCore.ensureField();
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null && level.getChunk(chunkX, chunkZ, ChunkStatus.FULL, false) instanceof LevelChunk chunk) {
            try {
                XaeroWorldMapCore.chunkCleanField.set(chunk, false);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
