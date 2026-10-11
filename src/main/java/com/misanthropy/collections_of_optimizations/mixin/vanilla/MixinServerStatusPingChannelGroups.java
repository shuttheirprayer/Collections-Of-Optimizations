package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.ServerStatusPing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(value = ServerStatusPing.class, remap = false)
public abstract class MixinServerStatusPingChannelGroups {

    @WrapOperation(
            method = "toBuf",
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/network/ServerStatusPing;getChannelsForMod(Ljava/lang/String;)Ljava/util/List;"),
            require = 0
    )
    private List<Map.Entry<ResourceLocation, ServerStatusPing.ChannelData>> coo$groupedChannels(
            ServerStatusPing ping, String modId,
            Operation<List<Map.Entry<ResourceLocation, ServerStatusPing.ChannelData>>> original,
            @Share("coo$channelsByNamespace") LocalRef<Map<String, List<Map.Entry<ResourceLocation, ServerStatusPing.ChannelData>>>> shared) {
        if (!CoOConfig.forgeStatusPingChannelGroups) {
            return original.call(ping, modId);
        }
        Map<String, List<Map.Entry<ResourceLocation, ServerStatusPing.ChannelData>>> groups = shared.get();
        if (groups == null) {
            groups = new HashMap<>();
            for (Map.Entry<ResourceLocation, ServerStatusPing.ChannelData> entry : ping.channels().entrySet()) {
                groups.computeIfAbsent(entry.getKey().getNamespace(), namespace -> new ArrayList<>()).add(entry);
            }
            shared.set(groups);
        }
        List<Map.Entry<ResourceLocation, ServerStatusPing.ChannelData>> channels = groups.get(modId);
        return channels != null ? channels : List.of();
    }
}
