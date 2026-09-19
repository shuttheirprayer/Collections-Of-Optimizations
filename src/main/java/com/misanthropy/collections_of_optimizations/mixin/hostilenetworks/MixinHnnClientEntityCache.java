package com.misanthropy.collections_of_optimizations.mixin.hostilenetworks;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(targets = "dev.shadowsoffire.hostilenetworks.util.ClientEntityCache", remap = false)
public abstract class MixinHnnClientEntityCache {

    @Shadow
    @Final
    private static Map<Level, Map<EntityType<?>, LivingEntity>> CACHE;

    @Inject(
            method = "computeIfAbsent",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$readCachedEntity(EntityType<?> type, Level level, CompoundTag nbt,
                                             CallbackInfoReturnable<LivingEntity> cir) {
        if (!CoOConfig.hostilenetworksLeanEntityCache || CACHE == null) {
            return;
        }
        Map<EntityType<?>, LivingEntity> byType = CACHE.get(level);
        if (byType == null) {
            return;
        }
        LivingEntity cached = byType.get(type);
        if (cached != null) {
            cir.setReturnValue(cached);
        }
    }

    @Inject(
            method = "tick",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$skipEmptyEntityTick(TickEvent.ClientTickEvent event, CallbackInfo ci) {
        if (!CoOConfig.hostilenetworksLeanEntityCache || CACHE == null) {
            return;
        }
        if (CACHE.isEmpty()) {
            ci.cancel();
        }
    }
}
