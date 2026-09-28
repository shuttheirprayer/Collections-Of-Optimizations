package com.misanthropy.collections_of_optimizations.mixin.soulsweapons;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.soulsweaponry.util.WeaponUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(value = WeaponUtil.class, remap = false)
public abstract class MixinWeaponUtilModPresence {

    @Unique
    private static final ConcurrentHashMap<String, Optional<ModFileInfo>> coo$modFiles = new ConcurrentHashMap<>();

    @Redirect(
            method = "isModLoaded",
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/loading/LoadingModList;getModFileById(Ljava/lang/String;)Lnet/minecraftforge/fml/loading/moddiscovery/ModFileInfo;"),
            require = 0
    )
    private static ModFileInfo coo$cachedModFile(LoadingModList modList, String modId) {
        if (!CoOConfig.soulsweaponsCacheModPresence) {
            return modList.getModFileById(modId);
        }
        Optional<ModFileInfo> cached = coo$modFiles.get(modId);
        if (cached == null) {
            cached = Optional.ofNullable(modList.getModFileById(modId));
            coo$modFiles.put(modId, cached);
        }
        return cached.orElse(null);
    }
}
