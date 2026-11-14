package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.DelegatingPackIndex;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.resource.DelegatingPackResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Mixin(value = DelegatingPackResources.class, remap = false)
public abstract class MixinDelegatingPackResourcesIndex {

    @Unique
    private static final boolean COO$CHECK = Boolean.getBoolean("coo.delegatingIndexCheck");

    @Unique
    private static final AtomicLong COO$CHECKED = new AtomicLong();

    @Unique
    private static final AtomicLong COO$MISMATCHES = new AtomicLong();

    @Unique
    private final ConcurrentHashMap<String, DelegatingPackIndex> coo$assetIndexes = new ConcurrentHashMap<>();

    @Unique
    private final ConcurrentHashMap<String, DelegatingPackIndex> coo$dataIndexes = new ConcurrentHashMap<>();

    @WrapOperation(
            method = "m_214146_",
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/resource/DelegatingPackResources;getCandidatePacks(Lnet/minecraft/server/packs/PackType;Lnet/minecraft/resources/ResourceLocation;)Ljava/util/List;"),
            require = 0
    )
    private List<PackResources> coo$indexedCandidates(DelegatingPackResources self, PackType type, ResourceLocation location,
                                                      Operation<List<PackResources>> original) {
        List<PackResources> packs = original.call(self, type, location);
        if (!CoOConfig.vanillaIndexDelegatingPacks || !CoOConfig.vanillaIndexModFiles || packs.size() < 4) {
            return packs;
        }
        ConcurrentHashMap<String, DelegatingPackIndex> indexes = type == PackType.CLIENT_RESOURCES ? coo$assetIndexes : coo$dataIndexes;
        String namespace = location.getNamespace();
        DelegatingPackIndex index = indexes.get(namespace);
        if (index == null || index.stale(packs)) {
            index = DelegatingPackIndex.build(type.getDirectory(), namespace, packs);
            indexes.put(namespace, index);
        }
        List<PackResources> picked = index.candidates(location);
        if (COO$CHECK && picked != packs) {
            coo$check(packs, picked, type, location);
        }
        return picked;
    }

    @Unique
    private static void coo$check(List<PackResources> packs, List<PackResources> picked, PackType type, ResourceLocation location) {
        PackResources expected = null;
        for (PackResources pack : packs) {
            if (pack.getResource(type, location) != null) {
                expected = pack;
                break;
            }
        }
        PackResources got = null;
        for (PackResources pack : picked) {
            if (pack.getResource(type, location) != null) {
                got = pack;
                break;
            }
        }
        long checked = COO$CHECKED.incrementAndGet();
        if (expected != got) {
            org.slf4j.LoggerFactory.getLogger("collections_of_optimizations").warn("Delegating (such a cool word) pack index mismatch for {} {}: expected {}, got {}",
                    type, location, expected == null ? "none" : expected.packId(), got == null ? "none" : got.packId());
            COO$MISMATCHES.incrementAndGet();
        }
        if ((checked & 0x3FFF) == 0) {
            org.slf4j.LoggerFactory.getLogger("collections_of_optimizations").info("Delegating pack index check: {} lookups compared, {} mismatches", checked, COO$MISMATCHES.get());
        }
    }
}
