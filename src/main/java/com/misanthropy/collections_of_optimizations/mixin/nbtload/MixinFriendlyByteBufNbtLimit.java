package com.misanthropy.collections_of_optimizations.mixin.nbtload;

import com.misanthropy.collections_of_optimizations.core.NbtFastLoad;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.network.FriendlyByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FriendlyByteBuf.class)
public abstract class MixinFriendlyByteBufNbtLimit {

    @Shadow
    public abstract CompoundTag readNbt(NbtAccounter accounter);

    @Inject(method = "readNbt()Lnet/minecraft/nbt/CompoundTag;", at = @At("HEAD"), cancellable = true)
    private void coo$unlimitedNbt(CallbackInfoReturnable<CompoundTag> cir) {
        if (NbtFastLoad.removeByteBufTagLimit) {
            cir.setReturnValue(this.readNbt(NbtAccounter.UNLIMITED));
        }
    }
}
