package com.misanthropy.collections_of_optimizations.mixin.brutality;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.goo.brutality.util.SealUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.HashMap;
import java.util.Map;

@Mixin(targets = "net.goo.brutality.entity.capabilities.EntityCapabilities$EntitySealTypeCap", remap = false)
public abstract class MixinBrutalitySealTypeNoThrow {

    @Unique
    private static final Map<String, SealUtils.SEAL_TYPE> coo$byName = new HashMap<>();

    static {
        for (SealUtils.SEAL_TYPE type : SealUtils.SEAL_TYPE.values()) {
            coo$byName.put(type.name(), type);
        }
    }

    @Redirect(
            method = "deserializeNBT(Lnet/minecraft/nbt/CompoundTag;)V",
            at = @At(value = "INVOKE", target = "Lnet/goo/brutality/util/SealUtils$SEAL_TYPE;valueOf(Ljava/lang/String;)Lnet/goo/brutality/util/SealUtils$SEAL_TYPE;"),
            require = 0
    )
    private SealUtils.SEAL_TYPE coo$lookupWithoutThrowing(String name) {
        if (!CoOConfig.brutalitySealTypeNoThrow) {
            return SealUtils.SEAL_TYPE.valueOf(name);
        }
        SealUtils.SEAL_TYPE type = coo$byName.get(name);
        return type != null ? type : SealUtils.SEAL_TYPE.NONE;
    }
}
