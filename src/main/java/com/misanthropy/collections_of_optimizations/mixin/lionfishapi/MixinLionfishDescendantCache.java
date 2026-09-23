package com.misanthropy.collections_of_optimizations.mixin.lionfishapi;

import com.github.L_Ender.lionfishapi.client.model.tools.AdvancedEntityModel;
import com.github.L_Ender.lionfishapi.client.model.tools.AdvancedModelBox;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Mixin(value = AdvancedEntityModel.class, remap = false)
public abstract class MixinLionfishDescendantCache {

    @Unique
    private final Map<String, Optional<AdvancedModelBox>> coo$descendants = new HashMap<>();

    @Inject(method = "getAnyDescendantWithName", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$cachedDescendant(String key, CallbackInfoReturnable<Optional<AdvancedModelBox>> cir) {
        if (!CoOConfig.lionfishapiCacheModelDescendants) {
            return;
        }
        Optional<AdvancedModelBox> found = coo$descendants.get(key);
        if (found == null) {
            found = Optional.empty();
            for (AdvancedModelBox box : ((AdvancedEntityModel<?>) (Object) this).getAllParts()) {
                if (Objects.equals(box.boxName, key)) {
                    found = Optional.of(box);
                    break;
                }
            }
            coo$descendants.put(key, found);
        }
        cir.setReturnValue(found);
    }
}
