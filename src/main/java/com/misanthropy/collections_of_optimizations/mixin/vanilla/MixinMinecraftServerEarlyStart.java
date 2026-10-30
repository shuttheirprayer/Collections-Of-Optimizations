package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServerEarlyStart {
    @Unique
    private static final int COO$SPAWN_AREA = 441;
    @Unique
    private static final int COO$CORE = 49;

    @WrapOperation(method = "prepareLevels", require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerChunkCache;getTickingGenerated()I"))
    private int coo$startWithCore(ServerChunkCache cache, Operation<Integer> original) {
        int ticking = original.call(cache);
        return CoOConfig.vanillaEarlyStart && ticking >= COO$CORE ? COO$SPAWN_AREA : ticking;
    }
}
