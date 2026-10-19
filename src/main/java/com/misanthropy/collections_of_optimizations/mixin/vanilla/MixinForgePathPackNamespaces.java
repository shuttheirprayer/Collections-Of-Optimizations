package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PackResources;
import net.minecraftforge.resource.PathPackResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

@Mixin(value = PathPackResources.class, remap = false)
public abstract class MixinForgePathPackNamespaces {

    @Unique
    private volatile Set<String> coo$assetNamespaces;

    @Unique
    private volatile Set<String> coo$dataNamespaces;

    @Shadow
    protected abstract Path resolve(String... paths);

    @Shadow
    private Set<String> getNamespacesFromDisk(PackType type) {
        throw new AssertionError();
    }

    @Unique
    private Set<String> coo$knownNamespaces(PackType type, Operation<Set<String>> original) {
        boolean assets = type == PackType.CLIENT_RESOURCES;
        Set<String> known = assets ? coo$assetNamespaces : coo$dataNamespaces;
        if (known != null) {
            return known;
        }
        if (resolve(type.getDirectory()).getFileSystem() == FileSystems.getDefault()) {
            return null;
        }
        known = original.call(type);
        if (assets) {
            coo$assetNamespaces = known;
        } else {
            coo$dataNamespaces = known;
        }
        return known;
    }

    @WrapMethod(method = "getNamespacesFromDisk", require = 0)
    private Set<String> coo$cacheNamespaces(PackType type, Operation<Set<String>> original) {
        if (!CoOConfig.vanillaCachePackNamespaces) {
            return original.call(type);
        }
        Set<String> known = coo$knownNamespaces(type, original);
        if (known == null) {
            return original.call(type);
        }
        Set<String> copy = new HashSet<>();
        copy.addAll(known);
        return copy;
    }

    @Inject(
            method = "m_8031_(Lnet/minecraft/server/packs/PackType;Ljava/lang/String;Ljava/lang/String;Lnet/minecraft/server/packs/PackResources$ResourceOutput;)V",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void coo$skipAbsentNamespace(PackType type, String namespace, String path, PackResources.ResourceOutput output, CallbackInfo ci) {
        if (!CoOConfig.vanillaSkipAbsentPackNamespaces || !CoOConfig.vanillaCachePackNamespaces) {
            return;
        }
        boolean assets = type == PackType.CLIENT_RESOURCES;
        Set<String> known = assets ? coo$assetNamespaces : coo$dataNamespaces;
        if (known == null) {
            if (resolve(type.getDirectory()).getFileSystem() == FileSystems.getDefault()) {
                return;
            }
            try {
                getNamespacesFromDisk(type);
            } catch (RuntimeException e) {
                return;
            }
            known = assets ? coo$assetNamespaces : coo$dataNamespaces;
        }
        if (known != null && !known.contains(namespace)) {
            ci.cancel();
        }
    }
}
