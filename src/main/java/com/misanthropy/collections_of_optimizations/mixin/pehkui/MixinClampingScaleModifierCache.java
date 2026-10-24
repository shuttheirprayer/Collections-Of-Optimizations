package com.misanthropy.collections_of_optimizations.mixin.pehkui;

import com.misanthropy.collections_of_optimizations.core.PehkuiClampBounds;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.util.ClampingScaleModifier;

import java.util.function.Supplier;

@Mixin(value = ClampingScaleModifier.class, remap = false)
public abstract class MixinClampingScaleModifierCache {

    @Shadow @Final private Supplier<Double> min;
    @Shadow @Final private Supplier<Double> max;

    @Unique
    private PehkuiClampBounds coo$bounds;

    @Overwrite
    public float modifyScale(ScaleData scaleData, float modifiedScale, float delta) {
        PehkuiClampBounds bounds = coo$bounds();
        return Math.max(Math.min(modifiedScale, bounds.max()), bounds.min());
    }

    @Overwrite
    public float modifyPrevScale(ScaleData scaleData, float modifiedScale) {
        PehkuiClampBounds bounds = coo$bounds();
        return Math.max(Math.min(modifiedScale, bounds.max()), bounds.min());
    }

    @Unique
    private PehkuiClampBounds coo$bounds() {
        long second = System.currentTimeMillis() >>> 10;
        PehkuiClampBounds bounds = this.coo$bounds;
        if (bounds == null || bounds.second() != second) {
            bounds = new PehkuiClampBounds(second, this.min.get().floatValue(), this.max.get().floatValue());
            this.coo$bounds = bounds;
        }
        return bounds;
    }
}
