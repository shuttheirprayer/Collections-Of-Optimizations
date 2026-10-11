package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.nio.ByteOrder;

@Mixin(ClientboundCustomPayloadPacket.class)
public abstract class MixinCustomPayloadHeapCopy {

    @SuppressWarnings("deprecation")
    @WrapOperation(
            method = "getData",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/FriendlyByteBuf;copy()Lio/netty/buffer/ByteBuf;", remap = false),
            require = 0
    )
    private ByteBuf coo$heapCopy(FriendlyByteBuf data, Operation<ByteBuf> original) {
        if (!CoOConfig.forgeCustomPayloadHeapCopy || data.order() != ByteOrder.BIG_ENDIAN) {
            return original.call(data);
        }
        int length = data.readableBytes();
        ByteBuf copy = Unpooled.buffer(length, data.maxCapacity());
        copy.writeBytes(data, data.readerIndex(), length);
        return copy;
    }
}
