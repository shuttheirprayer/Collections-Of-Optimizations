package com.misanthropy.collections_of_optimizations.core;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TagsUpdatedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.transformer.meta.MixinMerged;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

public final class ElysiumSoundTransformers {

    public static final String ELYSIUM_BLOCK_MIXIN = "net.jadenxgamer.elysium_api.impl.mixin.block.BlockMixin";

    private static final Logger LOGGER = LogManager.getLogger("collections_of_optimizations");
    private static final Object NONE = new Object();
    private static final AtomicInteger GENERATION = new AtomicInteger(1);

    private static volatile Object lastAccess;
    private static volatile int resolved;
    private static MethodHandle accessGetter;
    private static MethodHandle blocksGetter;
    private static MethodHandle soundTypeGetter;
    private static MethodHandle handler;
    private static ResourceKey<Registry<Object>> registryKey;

    private record Entry(int generation, Object transformer) {
    }

    private ElysiumSoundTransformers() {
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener(ElysiumSoundTransformers::onTagsUpdated);
    }

    private static void onTagsUpdated(TagsUpdatedEvent event) {
        GENERATION.incrementAndGet();
    }

    public static void dispatch(Block block, BlockState state, CallbackInfoReturnable<SoundType> cir) {
        try {
            int status = resolved == 0 ? resolve() : resolved;
            if (status < 0 || !CoOConfig.elysiumapiCacheSoundTransformers || !(block instanceof ElysiumSoundHolder holder)) {
                if (handler != null) {
                    handler.invoke(block, state, cir);
                }
                return;
            }
            RegistryAccess access = (RegistryAccess) accessGetter.invoke();
            if (access == null) {
                return;
            }
            if (access != lastAccess) {
                lastAccess = access;
                GENERATION.incrementAndGet();
            }
            int generation = GENERATION.get();
            Object transformer;
            if (holder.coo$getElysiumSound() instanceof Entry entry && entry.generation() == generation) {
                transformer = entry.transformer();
            } else {
                transformer = find(access, state);
                holder.coo$setElysiumSound(new Entry(generation, transformer));
            }
            if (transformer != NONE) {
                cir.setReturnValue((SoundType) soundTypeGetter.invoke(transformer));
            }
        } catch (Throwable throwable) {
            throw ElysiumSoundTransformers.<RuntimeException>sneaky(throwable);
        }
    }

    @SuppressWarnings("unchecked")
    private static Object find(RegistryAccess access, BlockState state) throws Throwable {
        Holder<Block> block = state.getBlockHolder();
        for (Object transformer : access.registryOrThrow(registryKey)) {
            if (((HolderSet<Block>) blocksGetter.invoke(transformer)).contains(block)) {
                return transformer;
            }
        }
        return NONE;
    }

    @SuppressWarnings("unchecked")
    private static synchronized int resolve() {
        if (resolved != 0) {
            return resolved;
        }
        int status;
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            for (Method method : Block.class.getMethods()) {
                MixinMerged merged = method.getAnnotation(MixinMerged.class);
                Class<?>[] params = method.getParameterTypes();
                if (merged != null && ELYSIUM_BLOCK_MIXIN.equals(merged.mixin()) && params.length == 2
                        && params[0] == BlockState.class && params[1] == CallbackInfoReturnable.class) {
                    handler = lookup.unreflect(method);
                    break;
                }
            }
            ClassLoader loader = Block.class.getClassLoader();
            Class<?> elysium = Class.forName("net.jadenxgamer.elysium_api.Elysium", true, loader);
            Class<?> registries = Class.forName("net.jadenxgamer.elysium_api.impl.registry.ElysiumRegistries", true, loader);
            Class<?> transformer = Class.forName("net.jadenxgamer.elysium_api.impl.sound_transformer.SoundTransformer", true, loader);
            accessGetter = lookup.findStaticGetter(elysium, "registryAccess", RegistryAccess.class);
            registryKey = (ResourceKey<Registry<Object>>) lookup.findStaticGetter(registries, "BLOCK_SOUND_TRANSFORMER", ResourceKey.class).invoke();
            blocksGetter = lookup.findVirtual(transformer, "blocks", MethodType.methodType(HolderSet.class));
            soundTypeGetter = lookup.findVirtual(transformer, "toSoundType", MethodType.methodType(SoundType.class));
            status = handler != null && registryKey != null ? 1 : -1;
        } catch (Throwable throwable) {
            LOGGER.warn("Collections of Optimizations: Elysium sound transformer cache is off ({})", throwable.toString());
            status = -1;
        }
        resolved = status;
        return status;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> T sneaky(Throwable throwable) throws T {
        throw (T) throwable;
    }
}
