package com.misanthropy.collections_of_optimizations.mixin.shouldersurfing;

import com.github.exopandora.shouldersurfing.client.event.handler.ComputePlayerAimStateEventHandlerImpl;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

@Mixin(value = ComputePlayerAimStateEventHandlerImpl.class, remap = false)
public abstract class MixinComputePlayerAimState {

    @Unique
    private static final Map<String, Predicate<String>> coo$PREDICATES = new ConcurrentHashMap<>();

    @WrapMethod(method = "expressionToMatchPredicate", require = 0)
    private static Predicate<String> coo$memoPredicate(String expression, Operation<Predicate<String>> original) {
        if (!CoOConfig.shouldersurfingMemoAdaptiveItemPatterns) {
            return original.call(expression);
        }
        Predicate<String> cached = coo$PREDICATES.get(expression);
        if (cached == null) {
            cached = original.call(expression);
            coo$PREDICATES.put(expression, cached);
        }
        return cached;
    }
}
