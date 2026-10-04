package com.misanthropy.collections_of_optimizations.mixin.lionfishapi;

import com.github.L_Ender.lionfishapi.client.model.Animations.ModelAnimator;
import com.github.L_Ender.lionfishapi.client.model.Transform;
import com.github.L_Ender.lionfishapi.client.model.tools.AdvancedModelBox;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Mixin(value = ModelAnimator.class, remap = false)
public abstract class MixinLionfishModelAnimator {

    @Shadow
    private HashMap<AdvancedModelBox, Transform> transformMap;

    @Shadow
    private HashMap<AdvancedModelBox, Transform> prevTransformMap;

    @Unique
    private final ArrayList<Transform> coo$pool = new ArrayList<>();

    @Redirect(
            method = "getTransform",
            at = @At(value = "INVOKE", target = "Ljava/util/HashMap;computeIfAbsent(Ljava/lang/Object;Ljava/util/function/Function;)Ljava/lang/Object;"),
            require = 0
    )
    private Object coo$pooledTransform(HashMap<Object, Object> map, Object box, Function<Object, Object> factory) {
        if (!CoOConfig.lionfishapiLeanModelAnimator) {
            return map.computeIfAbsent(box, factory);
        }
        Object transform = map.get(box);
        if (transform == null) {
            ArrayList<Transform> pool = this.coo$pool;
            transform = pool.isEmpty() ? new Transform() : pool.remove(pool.size() - 1);
            map.put(box, transform);
        }
        return transform;
    }

    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Ljava/util/HashMap;clear()V"), require = 0)
    private void coo$recycleOnUpdate(HashMap<AdvancedModelBox, Transform> map) {
        this.coo$recycle(map);
    }

    @Redirect(method = "endKeyframe(Z)V", at = @At(value = "INVOKE", target = "Ljava/util/HashMap;clear()V", ordinal = 0), require = 0)
    private void coo$recycleOnKeyframe(HashMap<AdvancedModelBox, Transform> map) {
        this.coo$recycle(map);
    }

    @Redirect(method = "endKeyframe(Z)V", at = @At(value = "INVOKE", target = "Ljava/util/HashMap;putAll(Ljava/util/Map;)V"), require = 0)
    private void coo$swapMaps(HashMap<AdvancedModelBox, Transform> prev, Map<AdvancedModelBox, Transform> current) {
        if (!CoOConfig.lionfishapiLeanModelAnimator || prev != this.prevTransformMap || current != this.transformMap || !prev.isEmpty()) {
            prev.putAll(current);
            return;
        }
        this.prevTransformMap = this.transformMap;
        this.transformMap = prev;
    }

    @Unique
    private void coo$recycle(HashMap<AdvancedModelBox, Transform> map) {
        if (CoOConfig.lionfishapiLeanModelAnimator && !map.isEmpty()) {
            for (Transform transform : map.values()) {
                transform.resetRotation();
                transform.resetOffset();
                this.coo$pool.add(transform);
            }
        }
        map.clear();
    }
}
