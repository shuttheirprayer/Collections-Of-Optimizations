package com.misanthropy.collections_of_optimizations.mixin.pehkui;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ScaleTypeSlot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleType;
import virtuoel.pehkui.api.ScaleTypes;
import virtuoel.pehkui.util.ScaleCachingUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(value = ScaleCachingUtils.class, remap = false)
public abstract class MixinScaleCachingUtils {

    @Shadow
    @Final
    @Mutable
    public static ScaleType[] CACHED;

    @Unique
    private static int coo$pehkuiOwnSlots;

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void coo$slotEveryBuiltinType(CallbackInfo ci) {
        List<ScaleType> types = new ArrayList<>(Arrays.asList(CACHED));
        coo$pehkuiOwnSlots = types.size();
        for (Field field : ScaleTypes.class.getFields()) {
            if (!Modifier.isStatic(field.getModifiers()) || field.getType() != ScaleType.class) {
                continue;
            }
            try {
                ScaleType type = (ScaleType) field.get(null);
                if (type != null && !types.contains(type)) {
                    types.add(type);
                }
            } catch (IllegalAccessException ignored) {
            }
        }
        for (int i = 0; i < types.size(); i++) {
            ((ScaleTypeSlot) types.get(i)).coo$setSlot(i);
        }
        CACHED = types.toArray(new ScaleType[0]);
    }

    @Overwrite
    public static ScaleData getCachedData(ScaleData[] cache, ScaleType type) {
        int slot = coo$usableSlot(cache, type);
        return slot < 0 ? null : cache[slot];
    }

    @Overwrite
    public static void setCachedData(ScaleData[] cache, ScaleType type, ScaleData data) {
        int slot = coo$usableSlot(cache, type);
        if (slot >= 0) {
            cache[slot] = data;
        }
    }

    @Unique
    private static int coo$usableSlot(ScaleData[] cache, ScaleType type) {
        int slot = ((ScaleTypeSlot) type).coo$slot();
        if (slot < 0 || slot >= cache.length || (slot >= coo$pehkuiOwnSlots && !CoOConfig.pehkuiCacheEveryScaleType)) {
            return -1;
        }
        return slot;
    }
}
