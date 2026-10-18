package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ZipPackGroups;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Mixin(FilePackResources.class)
public abstract class MixinFilePackResourcesIndex {

    @Unique
    private static final Logger COO$LOGGER = LogUtils.getLogger();

    @Unique
    private volatile ZipPackGroups coo$groups;

    @Shadow
    @Nullable
    private ZipFile getOrCreateZipFile() {
        return null;
    }

    @Inject(method = "listResources", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$indexedListResources(PackType type, String namespace, String path, PackResources.ResourceOutput output, CallbackInfo ci) {
        if (!CoOConfig.vanillaIndexZipPacks || path.isEmpty()) {
            return;
        }
        ZipFile zip = getOrCreateZipFile();
        if (zip == null) {
            return;
        }
        ZipPackGroups groups = coo$groups;
        if (groups == null || !groups.covers(zip)) {
            synchronized (this) {
                groups = coo$groups;
                if (groups == null || !groups.covers(zip)) {
                    groups = ZipPackGroups.build(zip);
                    coo$groups = groups;
                }
            }
        }
        ci.cancel();
        String root = type.getDirectory() + "/" + namespace + "/";
        String prefix = root + path + "/";
        for (ZipEntry entry : groups.entriesUnder(prefix)) {
            String name = entry.getName();
            if (!name.startsWith(prefix)) {
                continue;
            }
            String relative = name.substring(root.length());
            ResourceLocation id = ResourceLocation.tryBuild(namespace, relative);
            if (id != null) {
                output.accept(id, IoSupplier.create(zip, entry));
            } else {
                COO$LOGGER.warn("Invalid path in datapack: {}:{}, ignoring", namespace, relative);
            }
        }
    }

    @Inject(method = "close", at = @At("HEAD"), require = 0)
    private void coo$dropIndex(CallbackInfo ci) {
        coo$groups = null;
    }
}
