package com.misanthropy.collections_of_optimizations.mixin.xaeroworldmap;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.map.core.XaeroWorldMapCore;

import java.lang.reflect.Field;

@Mixin(value = XaeroWorldMapCore.class, remap = false)
public abstract class MixinXaeroWorldMapCoreChunkCleanField {

    @Unique
    private static Field coo$openedField;

    @Inject(method = "ensureField", at = @At("RETURN"), require = 0)
    private static void coo$skipAccessChecks(CallbackInfo ci) {
        Field field = XaeroWorldMapCore.chunkCleanField;
        if (field != null && field != coo$openedField && CoOConfig.xaeroworldmapOpenReflectedFields) {
            field.setAccessible(true);
            coo$openedField = field;
        }
    }
}
