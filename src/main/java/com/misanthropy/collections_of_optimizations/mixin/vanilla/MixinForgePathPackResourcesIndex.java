package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ModFileIndex;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraftforge.resource.PathPackResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.nio.file.FileSystems;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.List;

@Mixin(value = PathPackResources.class, remap = false)
public abstract class MixinForgePathPackResourcesIndex {

    @Unique
    private static final Logger COO$LOGGER = LoggerFactory.getLogger("collections_of_optimizations");

    @Shadow
    protected abstract Path resolve(String... paths);

    @Shadow
    public abstract Path getSource();

    @Unique
    private ModFileIndex coo$index() {
        Path source = getSource();
        if (source == null || source.getFileSystem() == FileSystems.getDefault() && !source.toString().endsWith(".jar")) {
            return null;
        }
        if (CoOConfig.vanillaIndexModFilesInBackground) {
            ModFileIndex index = ModFileIndex.cached(source);
            if (index == null) {
                ModFileIndex.buildInBackground(source, this::resolve);
            }
            return index;
        }
        return ModFileIndex.getOrBuild(source, this::resolve);
    }

    @WrapOperation(
            method = "m_8017_",
            at = @At(value = "INVOKE", target = "Ljava/nio/file/Files;exists(Ljava/nio/file/Path;[Ljava/nio/file/LinkOption;)Z"),
            require = 0
    )
    private boolean coo$indexedExists(Path path, LinkOption[] options, Operation<Boolean> original, @Local(argsOnly = true) String[] paths) {
        if (!CoOConfig.vanillaIndexModFiles || paths.length < 3 || !ModFileIndex.indexed(paths[0])
                || path.getFileSystem() == FileSystems.getDefault()) {
            return original.call(path, options);
        }
        ModFileIndex index = coo$index();
        if (index != null && index.usable() && !index.contains(paths)) {
            return false;
        }
        return original.call(path, options);
    }

    @WrapOperation(
            method = "lambda$listResources$0",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/PathPackResources;m_246914_(Ljava/lang/String;Ljava/nio/file/Path;Ljava/util/List;Lnet/minecraft/server/packs/PackResources$ResourceOutput;)V"),
            require = 0
    )
    private void coo$indexedList(String namespace, Path root, List<String> parts, PackResources.ResourceOutput output,
                                 Operation<Void> original, @Local(argsOnly = true) PackType type) {
        if (!CoOConfig.vanillaIndexModFileListing || root.getFileSystem() == FileSystems.getDefault()) {
            original.call(namespace, root, parts, output);
            return;
        }
        ModFileIndex index = coo$index();
        if (index == null || !index.usable()) {
            original.call(namespace, root, parts, output);
            return;
        }
        index.list(type.getDirectory(), namespace, parts, relative -> {
            ResourceLocation id = ResourceLocation.tryBuild(namespace, relative);
            if (id == null) {
                COO$LOGGER.error("Invalid path in pack: {}:{}, ignoring", namespace, relative);
            } else {
                output.accept(id, IoSupplier.create(root.resolve(relative)));
            }
        });
    }
}
