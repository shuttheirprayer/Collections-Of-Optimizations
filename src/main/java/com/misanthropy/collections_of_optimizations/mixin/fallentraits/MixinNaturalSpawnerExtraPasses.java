package com.misanthropy.collections_of_optimizations.mixin.fallentraits;

import com.bawnorton.mixinsquared.TargetHandler;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.FtSpawnBonus;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = NaturalSpawner.class, priority = 1500)
public abstract class MixinNaturalSpawnerExtraPasses {

    @TargetHandler(mixin = "net.kayn.fallen_traits.mixin.minecraft.NaturalSpawnerMixin", name = "fallen_traits$extraNaturalSpawnPasses")
    @Inject(method = "@MixinSquared:Handler", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void coo$cachedSpawnBonus(ServerLevel level, LevelChunk chunk, NaturalSpawner.SpawnState state,
                                             boolean spawnFriendlies, boolean spawnEnemies, boolean spawnPersistent,
                                             CallbackInfo original, CallbackInfo ci) {
        if (!CoOConfig.fallentraitsCacheSpawnBonus) {
            return;
        }
        ci.cancel();
        FtSpawnBonus.afterSpawn(level, chunk, state, spawnFriendlies, spawnEnemies, spawnPersistent);
    }
}
