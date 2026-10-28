package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IdMappingEvent;
import net.minecraftforge.registries.ObjectHolderRegistry;
import net.minecraftforge.registries.RegistryManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Mixin(value = GameData.class, remap = false)
public abstract class MixinGameDataIdenticalSnapshot {

    @Unique
    private static final Logger COO$LOGGER = LogManager.getLogger("collections_of_optimizations");

    @Shadow
    private static void fireRemapEvent(Map<ResourceLocation, Map<ResourceLocation, IdMappingEvent.IdRemapping>> remaps, boolean isFreezing) {
        throw new AssertionError();
    }

    @Inject(method = "injectSnapshot", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$skipIdenticalSnapshot(Map<ResourceLocation, ForgeRegistry.Snapshot> snapshot, boolean injectFrozenData, boolean isLocalWorld,
                                                  CallbackInfoReturnable<Multimap<ResourceLocation, ResourceLocation>> cir) {
        if (!CoOConfig.vanillaSkipIdenticalRegistrySnapshot || !injectFrozenData) {
            return;
        }
        long start = System.nanoTime();
        Map<ResourceLocation, ForgeRegistry<?>> active = ((RegistryManagerAccessor) RegistryManager.ACTIVE).coo$registries();
        Map<ResourceLocation, ForgeRegistry<?>> frozen = ((RegistryManagerAccessor) RegistryManager.FROZEN).coo$registries();
        if (active.size() != frozen.size()) {
            return;
        }
        Map<ResourceLocation, ForgeRegistry.Snapshot> current = new HashMap<>();
        for (Map.Entry<ResourceLocation, ForgeRegistry<?>> entry : active.entrySet()) {
            ForgeRegistry<?> frozenRegistry = frozen.get(entry.getKey());
            if (frozenRegistry == null) {
                return;
            }
            ForgeRegistry.Snapshot live = entry.getValue().makeSnapshot();
            if (!coo$sameIds(live, frozenRegistry.makeSnapshot())) {
                return;
            }
            current.put(entry.getKey(), live);
        }
        Map<ResourceLocation, ForgeRegistry.Snapshot> saved = new LinkedHashMap<>();
        for (Map.Entry<ResourceLocation, ForgeRegistry.Snapshot> entry : snapshot.entrySet()) {
            ResourceLocation name = RegistryManager.ACTIVE.updateLegacyName(entry.getKey());
            ForgeRegistry.Snapshot live = current.get(name);
            if (live == null || !coo$sameIds(live, entry.getValue())) {
                return;
            }
            saved.putIfAbsent(name, entry.getValue());
        }
        Map<ResourceLocation, Map<ResourceLocation, IdMappingEvent.IdRemapping>> remaps = new HashMap<>();
        for (Map.Entry<ResourceLocation, ForgeRegistry.Snapshot> entry : saved.entrySet()) {
            Map<ResourceLocation, ResourceLocation> aliases = ((ForgeRegistryAliasAccessor) active.get(entry.getKey())).coo$aliases();
            entry.getValue().aliases.forEach((src, dst) -> {
                if (!src.equals(dst)) {
                    aliases.putIfAbsent(src, dst);
                }
            });
            remaps.put(entry.getKey(), new LinkedHashMap<>());
        }
        COO$LOGGER.info("Registry ids in this save match the game, skipped re-injecting {} registries ({} ms)",
                remaps.size(), (System.nanoTime() - start) / 1_000_000);
        fireRemapEvent(remaps, false);
        ObjectHolderRegistry.applyObjectHolders();
        cir.setReturnValue(ArrayListMultimap.create());
    }

    @Unique
    private static boolean coo$sameIds(ForgeRegistry.Snapshot a, ForgeRegistry.Snapshot b) {
        return a.ids.equals(b.ids) && a.blocked.equals(b.blocked) && a.overrides.equals(b.overrides);
    }
}
