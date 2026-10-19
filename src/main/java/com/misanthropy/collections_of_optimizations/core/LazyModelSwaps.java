package com.misanthropy.collections_of_optimizations.core;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.embeddedt.modernfix.dynamicresources.DynamicBakedModelProvider;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public final class LazyModelSwaps {

    private static final Logger LOGGER = LogManager.getLogger("collections_of_optimizations");
    private static final String EMULATED_REGISTRY = "org.embeddedt.modernfix.forge.dynresources.ModelBakeEventHelper$EmulatedModelRegistry";

    private static final ConcurrentHashMap<ResourceLocation, Pending> PENDING = new ConcurrentHashMap<>();

    private LazyModelSwaps() {
    }

    public static boolean defer(Map<ResourceLocation, BakedModel> registry, ResourceLocation key, Function<BakedModel, ? extends BakedModel> factory) {
        if (!CoOConfig.hexereiLazyModelSwaps || !(DynamicBakedModelProvider.currentInstance instanceof LazySwapHook)) {
            return false;
        }
        String type = registry.getClass().getName();
        if (!EMULATED_REGISTRY.equals(type) && !(registry instanceof DynamicBakedModelProvider)) {
            return false;
        }
        Pending previous = PENDING.get(key);
        if (previous != null) {
            previous.resolve();
        }
        PENDING.put(key, new Pending(key, registry, factory));
        return true;
    }

    public static void resolve(Object key) {
        if (!PENDING.isEmpty() && key instanceof ResourceLocation) {
            Pending pending = PENDING.get(key);
            if (pending != null) {
                pending.resolve();
            }
        }
    }

    public static void resolveAll(Map<?, ?> keys) {
        if (!PENDING.isEmpty()) {
            for (Object key : keys.keySet()) {
                resolve(key);
            }
        }
    }

    public static void resolveEverything() {
        if (!PENDING.isEmpty()) {
            for (Pending pending : new ArrayList<>(PENDING.values())) {
                pending.resolve();
            }
        }
    }

    private static final class Pending {

        private final ResourceLocation key;
        private final Map<ResourceLocation, BakedModel> registry;
        private final Function<BakedModel, ? extends BakedModel> factory;
        private boolean done;
        private boolean resolving;

        private Pending(ResourceLocation key, Map<ResourceLocation, BakedModel> registry, Function<BakedModel, ? extends BakedModel> factory) {
            this.key = key;
            this.registry = registry;
            this.factory = factory;
        }

        private void resolve() {
            synchronized (this) {
                if (this.done || this.resolving) {
                    return;
                }
                this.resolving = true;
                try {
                    this.registry.put(this.key, this.factory.apply(this.registry.get(this.key)));
                } catch (Throwable e) {
                    LOGGER.error("Deferred model swap for {} failed", this.key, e);
                } finally {
                    this.done = true;
                    this.resolving = false;
                    PENDING.remove(this.key, this);
                }
            }
        }
    }
}
