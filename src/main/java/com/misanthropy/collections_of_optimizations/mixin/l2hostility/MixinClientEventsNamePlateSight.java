package com.misanthropy.collections_of_optimizations.mixin.l2hostility;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import dev.xkmc.l2hostility.events.ClientEvents;
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.lang.ref.WeakReference;

@Mixin(value = ClientEvents.class, remap = false)
public abstract class MixinClientEventsNamePlateSight {

    @Unique
    private static final Int2LongOpenHashMap coo$sightByEntity = new Int2LongOpenHashMap();

    @Unique
    private static WeakReference<Level> coo$sightLevel = new WeakReference<>(null);

    @WrapOperation(
            method = "renderNamePlate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;hasLineOfSight(Lnet/minecraft/world/entity/Entity;)Z",
                    remap = true
            ),
            require = 0
    )
    private static boolean coo$sightOncePerTick(LocalPlayer player, Entity target, Operation<Boolean> original) {
        if (!CoOConfig.l2hostilityNamePlateSightPerTick) {
            return original.call(player, target);
        }
        Level level = player.level();
        if (level != coo$sightLevel.get() || coo$sightByEntity.size() > 4096) {
            coo$sightByEntity.clear();
            coo$sightLevel = new WeakReference<>(level);
        }
        long stamp = level.getGameTime() << 1;
        long cached = coo$sightByEntity.getOrDefault(target.getId(), -1L);
        if (cached >= 0 && (cached & ~1L) == stamp) {
            return (cached & 1L) != 0;
        }
        boolean sees = original.call(player, target);
        coo$sightByEntity.put(target.getId(), stamp | (sees ? 1L : 0L));
        return sees;
    }
}
