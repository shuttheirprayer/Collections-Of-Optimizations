package com.misanthropy.collections_of_optimizations.mixin.fancymenu;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import de.keksuccino.fancymenu.util.file.LocalSourcePathResolver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.concurrent.ConcurrentHashMap;

@Mixin(value = LocalSourcePathResolver.class, remap = false)
public abstract class MixinLocalSourcePathMemo {

    @Unique
    private static final int coo$LIMIT = 4096;

    @Unique
    private final ConcurrentHashMap<String, LocalSourcePathResolver.ResolvedPath> coo$resolved = new ConcurrentHashMap<>();

    @WrapMethod(method = "resolve(Ljava/lang/String;)Lde/keksuccino/fancymenu/util/file/LocalSourcePathResolver$ResolvedPath;", require = 0)
    private LocalSourcePathResolver.ResolvedPath coo$memoResolve(String rawPath, Operation<LocalSourcePathResolver.ResolvedPath> original) {
        if (!CoOConfig.fancymenuMemoLocalSources || rawPath == null) {
            return original.call(rawPath);
        }
        LocalSourcePathResolver.ResolvedPath known = this.coo$resolved.get(rawPath);
        if (known != null) {
            return known;
        }
        LocalSourcePathResolver.ResolvedPath resolved = original.call(rawPath);
        if (resolved != null) {
            if (this.coo$resolved.size() >= coo$LIMIT) {
                this.coo$resolved.clear();
            }
            this.coo$resolved.put(rawPath, resolved);
        }
        return resolved;
    }
}
