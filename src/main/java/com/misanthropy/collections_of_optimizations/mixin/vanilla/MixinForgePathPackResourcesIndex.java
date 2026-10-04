package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ModFileIndex;
import net.minecraftforge.resource.PathPackResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.nio.file.FileSystems;
import java.nio.file.LinkOption;
import java.nio.file.Path;

@Mixin(value = PathPackResources.class, remap = false)
public abstract class MixinForgePathPackResourcesIndex {

    @Unique
    private volatile ModFileIndex coo$index;

    @Shadow
    protected abstract Path resolve(String... paths);

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
        ModFileIndex index = coo$index;
        if (index == null) {
            synchronized (this) {
                index = coo$index;
                if (index == null) {
                    index = ModFileIndex.build(this::resolve);
                    coo$index = index;
                }
            }
        }
        if (index.usable() && !index.mightContain(path)) {
            return false;
        }
        return original.call(path, options);
    }
}
