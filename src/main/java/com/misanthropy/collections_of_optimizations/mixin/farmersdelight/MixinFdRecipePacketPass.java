package com.misanthropy.collections_of_optimizations.mixin.farmersdelight;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.core.FdToolActionPass;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(ClientboundUpdateRecipesPacket.class)
public abstract class MixinFdRecipePacketPass {

    @WrapOperation(
            method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/FriendlyByteBuf;readList(Lnet/minecraft/network/FriendlyByteBuf$Reader;)Ljava/util/List;"
            ),
            require = 0
    )
    private List<?> coo$toolActionScanPass(FriendlyByteBuf buffer,
                                           FriendlyByteBuf.Reader<?> reader,
                                           Operation<List<?>> original) {
        FdToolActionPass.enter();
        try {
            return original.call(buffer, reader);
        } finally {
            FdToolActionPass.leave();
        }
    }
}
