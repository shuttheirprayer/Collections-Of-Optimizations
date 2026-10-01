package com.misanthropy.collections_of_optimizations.mixin.soulsweapons;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.soulsweaponry.util.WeaponUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(value = WeaponUtil.class, remap = false)
public abstract class MixinWeaponUtilModPresence {

    @Unique
    private static final ConcurrentHashMap<String, Optional<ModFileInfo>> coo$modFiles = new ConcurrentHashMap<>();

    @Unique
    private static byte coo$fightMod;

    @Shadow(remap = false)
    public static boolean isModLoaded(String modId) {
        throw new AssertionError();
    }

    @Overwrite(remap = false)
    public static boolean isFightModLoaded() {
        if (!CoOConfig.soulsweaponsCacheModPresence) {
            return isModLoaded("bettercombat") || isModLoaded("epicfight");
        }
        byte state = coo$fightMod;
        if (state == 0) {
            state = isModLoaded("bettercombat") || isModLoaded("epicfight") ? (byte) 1 : (byte) 2;
            coo$fightMod = state;
        }
        return state == 1;
    }

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
