package com.misanthropy.collections_of_optimizations.mixin.mahoutsukai;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ClientTickStamp;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashSet;

@Pseudo
@Mixin(targets = "stepsword.mahoutsukai.proxy.ClientProxy", remap = false)
public abstract class MixinMahouClientProxy {

    @Unique
    private static HashSet<Entity> coo$entities;

    @Unique
    private static ClientLevel coo$entitiesLevel;

    @Unique
    private static int coo$entitiesStamp = Integer.MIN_VALUE;

    @WrapMethod(method = "getAllEntities", require = 0)
    private static HashSet<Entity> coo$memoAllEntities(ClientLevel level, Operation<HashSet<Entity>> original) {
        if (!CoOConfig.mahoutsukaiMemoAllEntities) {
            return original.call(level);
        }
        int stamp = ClientTickStamp.currentOnRenderThread();
        if (stamp == -1) {
            return original.call(level);
        }
        HashSet<Entity> cached = coo$entities;
        if (cached != null && stamp == coo$entitiesStamp && level == coo$entitiesLevel) {
            return cached;
        }
        HashSet<Entity> value = original.call(level);
        coo$entities = value;
        coo$entitiesLevel = level;
        coo$entitiesStamp = stamp;
        return value;
    }
}
