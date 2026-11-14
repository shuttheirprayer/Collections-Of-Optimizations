package com.misanthropy.collections_of_optimizations.mixin.jei;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import mezz.jei.library.recipes.RecipeTransferManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

@Mixin(value = RecipeTransferManager.class, remap = false)
public abstract class MixinRecipeTransferManager {

    @Unique
    private static final Field coo$UNSUPPORTED_CONTAINERS = coo$findUnsupportedContainers();

    @Unique
    private static Field coo$findUnsupportedContainers() {
        try {
            Field field = RecipeTransferManager.class.getDeclaredField("unsupportedContainers");
            if (field.getType() != Set.class) {
                return null;
            }
            field.setAccessible(true);
            return field;
        } catch (ReflectiveOperationException | RuntimeException e) {
            return null;
        }
    }

    @Inject(method = "<init>", at = @At("RETURN"), require = 0)
    private void coo$weakUnsupportedContainers(CallbackInfo ci) {
        if (!CoOConfig.jeiWeakUnsupportedContainers || coo$UNSUPPORTED_CONTAINERS == null) {
            return;
        }
        try {
            if (((Set<?>) coo$UNSUPPORTED_CONTAINERS.get(this)).isEmpty()) {
                coo$UNSUPPORTED_CONTAINERS.set(this, Collections.newSetFromMap(new WeakHashMap<>()));
            }
        } catch (ReflectiveOperationException | RuntimeException ignored) {
        }
    }
}
