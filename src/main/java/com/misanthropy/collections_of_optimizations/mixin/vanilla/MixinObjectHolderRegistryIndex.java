package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolderRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.apache.logging.log4j.LogManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@Mixin(value = ObjectHolderRegistry.class, remap = false)
public abstract class MixinObjectHolderRegistryIndex {

    @Unique
    private static final Map<ResourceLocation, Set<Consumer<Predicate<ResourceLocation>>>> COO$BY_REGISTRY = new HashMap<>();

    @Unique
    private static final Set<Consumer<Predicate<ResourceLocation>>> COO$UNKNOWN = new LinkedHashSet<>();

    @Unique
    private static final Map<Class<?>, Function<Object, ResourceLocation>> COO$READERS = new HashMap<>();

    @Unique
    private static boolean coo$logged;

    @Inject(method = "addHandler", at = @At("TAIL"), require = 0)
    private static void coo$index(Consumer<Predicate<ResourceLocation>> ref, CallbackInfo ci) {
        ResourceLocation registry = coo$registryOf(ref);
        if (registry == null) {
            COO$UNKNOWN.add(ref);
        } else {
            COO$BY_REGISTRY.computeIfAbsent(registry, key -> new LinkedHashSet<>()).add(ref);
        }
    }

    @Inject(method = "removeHandler", at = @At("TAIL"), require = 0)
    private static void coo$unindex(Consumer<Predicate<ResourceLocation>> ref, CallbackInfoReturnable<Boolean> cir) {
        if (!COO$UNKNOWN.remove(ref)) {
            for (Set<Consumer<Predicate<ResourceLocation>>> holders : COO$BY_REGISTRY.values()) {
                if (holders.remove(ref)) {
                    break;
                }
            }
        }
    }

    @Inject(method = "applyObjectHolders(Ljava/util/function/Predicate;)V", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$onlyMatchingRegistries(Predicate<ResourceLocation> filter, CallbackInfo ci) {
        if (!CoOConfig.vanillaIndexedObjectHolders) {
            return;
        }
        List<Consumer<Predicate<ResourceLocation>>> holders = new ArrayList<>();
        synchronized (ObjectHolderRegistry.class) {
            int matched = 0;
            for (Map.Entry<ResourceLocation, Set<Consumer<Predicate<ResourceLocation>>>> entry : COO$BY_REGISTRY.entrySet()) {
                if (filter.test(entry.getKey())) {
                    matched++;
                    holders.addAll(entry.getValue());
                }
            }
            if (matched == COO$BY_REGISTRY.size()) {
                return;
            }
            holders.addAll(COO$UNKNOWN);
            if (!coo$logged) {
                coo$logged = true;
                int indexed = 0;
                for (Set<Consumer<Predicate<ResourceLocation>>> set : COO$BY_REGISTRY.values()) {
                    indexed += set.size();
                }
                LogManager.getLogger("collections_of_optimizations")
                        .info("Object holders sorted by registry: {} in {} registries, {} refreshed every time", indexed, COO$BY_REGISTRY.size(), COO$UNKNOWN.size());
            }
        }
        RuntimeException aggregate = new RuntimeException("Failed to apply some object holders, see suppressed exceptions for details");
        for (Consumer<Predicate<ResourceLocation>> holder : holders) {
            try {
                holder.accept(filter);
            } catch (Exception e) {
                aggregate.addSuppressed(e);
            }
        }
        if (aggregate.getSuppressed().length > 0) {
            throw aggregate;
        }
        ci.cancel();
    }

    @Unique
    private static ResourceLocation coo$registryOf(Object ref) {
        Function<Object, ResourceLocation> reader = COO$READERS.computeIfAbsent(ref.getClass(), MixinObjectHolderRegistryIndex::coo$readerFor);
        try {
            return reader == null ? null : reader.apply(ref);
        } catch (Throwable t) {
            return null;
        }
    }

    @Unique
    private static Function<Object, ResourceLocation> coo$readerFor(Class<?> type) {
        try {
            String name = type.getName();
            if (name.equals("net.minecraftforge.registries.ObjectHolderRef")) {
                Field field = type.getDeclaredField("registry");
                field.setAccessible(true);
                return ref -> coo$nameOf(coo$get(field, ref));
            }
            if (name.equals("net.minecraftforge.registries.RegistryObject$1")) {
                Field field = type.getDeclaredField("val$registryName");
                field.setAccessible(true);
                return ref -> (ResourceLocation) coo$get(field, ref);
            }
            if (name.startsWith("net.minecraftforge.registries.RegistryObject$$Lambda")) {
                for (Field field : type.getDeclaredFields()) {
                    if (IForgeRegistry.class.isAssignableFrom(field.getType())) {
                        field.setAccessible(true);
                        return ref -> coo$nameOf(coo$get(field, ref));
                    }
                }
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    @Unique
    private static Object coo$get(Field field, Object ref) {
        try {
            return field.get(ref);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    @Unique
    private static ResourceLocation coo$nameOf(Object registry) {
        return registry instanceof IForgeRegistry<?> forgeRegistry ? forgeRegistry.getRegistryName() : null;
    }
}
