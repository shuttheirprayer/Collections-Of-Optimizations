package com.misanthropy.collections_of_optimizations.core;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.biome.Climate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public final class ParameterListEncodeCache<T> implements Codec<Climate.ParameterList<T>> {
    private static final Logger LOGGER = LogManager.getLogger("collections_of_optimizations");
    private static final int VERIFIED_REUSES = 2;

    private static volatile boolean broken;

    private final Codec<Climate.ParameterList<T>> delegate;
    private final Map<Climate.ParameterList<T>, Encoded> encoded = Collections.synchronizedMap(new WeakHashMap<>());

    private static final class Encoded {
        final Object owner;
        final Object[] entries;
        final Tag tag;
        int reuses;

        Encoded(Object owner, Object[] entries, Tag tag) {
            this.owner = owner;
            this.entries = entries;
            this.tag = tag;
        }
    }

    private ParameterListEncodeCache(Codec<Climate.ParameterList<T>> delegate) {
        this.delegate = delegate;
    }

    public static <T> Codec<Climate.ParameterList<T>> wrap(Codec<Climate.ParameterList<T>> delegate) {
        return new ParameterListEncodeCache<>(delegate);
    }

    @Override
    public <R> DataResult<Pair<Climate.ParameterList<T>, R>> decode(DynamicOps<R> ops, R input) {
        return delegate.decode(ops, input);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> DataResult<R> encode(Climate.ParameterList<T> input, DynamicOps<R> ops, R prefix) {
        if (broken || !CoOConfig.vanillaReuseBiomeListEncoding || input == null || prefix != ops.empty()
                || !(prefix instanceof EndTag) || !(ops instanceof RegistryOps<R> registryOps)) {
            return delegate.encode(input, ops, prefix);
        }
        Object owner = registryOps.owner(Registries.BIOME).orElse(null);
        if (owner == null) {
            return delegate.encode(input, ops, prefix);
        }
        List<Pair<Climate.ParameterPoint, T>> values = input.values();
        Encoded known = encoded.get(input);
        if (known != null && known.owner == owner && same(values, known.entries)) {
            if (known.reuses < VERIFIED_REUSES) {
                DataResult<R> fresh = delegate.encode(input, ops, prefix);
                if (fresh.result().isEmpty() || !known.tag.equals(fresh.result().get())) {
                    broken = true;
                    LOGGER.warn("A reused biome list encoding differed from a fresh one!! Biome lists are encoded fresh from now on.");
                    return fresh;
                }
                known.reuses++;
                return fresh;
            }
            return DataResult.success((R) copyInOrder(known.tag));
        }
        DataResult<R> fresh = delegate.encode(input, ops, prefix);
        if (fresh.error().isEmpty() && fresh.result().isPresent() && fresh.result().get() instanceof Tag tag) {
            encoded.put(input, new Encoded(owner, values.toArray(), copyInOrder(tag)));
        }
        return fresh;
    }

    private static Tag copyInOrder(Tag tag) {
        if (tag instanceof CompoundTag compound) {
            CompoundTag copy = new CompoundTag();
            for (String key : compound.getAllKeys()) {
                copy.put(key, copyInOrder(compound.get(key)));
            }
            return copy;
        }
        if (tag instanceof ListTag list) {
            ListTag copy = new ListTag();
            for (Tag element : list) {
                copy.add(copyInOrder(element));
            }
            return copy;
        }
        return tag.copy();
    }

    private static boolean same(List<?> values, Object[] entries) {
        if (values.size() != entries.length) {
            return false;
        }
        for (int i = 0; i < entries.length; i++) {
            if (values.get(i) != entries[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return delegate.toString();
    }
}
