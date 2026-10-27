package com.misanthropy.collections_of_optimizations.mixin.architectury;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.forge.NetworkManagerImpl;
import dev.architectury.networking.transformers.PacketTransformer;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = NetworkManagerImpl.class, remap = false)
public abstract class MixinNetworkManagerConcurrentMaps {

    @WrapMethod(method = "registerS2CReceiver")
    private static void coo$lockedS2C(ResourceLocation id, List<PacketTransformer> transformers, NetworkManager.NetworkReceiver receiver,
                                      Operation<Void> original) {
        synchronized (NetworkManagerImpl.class) {
            original.call(id, transformers, receiver);
        }
    }

    @WrapMethod(method = "registerC2SReceiver")
    private static void coo$lockedC2S(ResourceLocation id, List<PacketTransformer> transformers, NetworkManager.NetworkReceiver receiver,
                                      Operation<Void> original) {
        synchronized (NetworkManagerImpl.class) {
            original.call(id, transformers, receiver);
        }
    }
}
