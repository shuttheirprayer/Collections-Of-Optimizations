package com.misanthropy.collections_of_optimizations.mixin.xaeroworldmap;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xaero.map.MapProcessor;

import java.lang.reflect.Field;

@Mixin(value = MapProcessor.class, remap = false)
public abstract class MixinMapProcessorScheduledTasksField {

    @Unique
    private boolean coo$scheduledTasksOpened;

    @Redirect(
            method = "getMinecraftScheduledTasks",
            at = @At(value = "INVOKE", target = "Ljava/lang/reflect/Field;setAccessible(Z)V"),
            require = 0
    )
    private void coo$openOnce(Field field, boolean flag) {
        if (!CoOConfig.xaeroworldmapOpenReflectedFields) {
            field.setAccessible(flag);
            return;
        }
        if (!this.coo$scheduledTasksOpened) {
            field.setAccessible(true);
            this.coo$scheduledTasksOpened = true;
        }
    }
}
