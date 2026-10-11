package com.misanthropy.collections_of_optimizations.mixin.nbtload;

import com.misanthropy.collections_of_optimizations.core.NbtFastLoad;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CompoundTag.class, priority = 1500)
public abstract class MixinCompoundTagFastLoad {

    @Shadow
    @Final
    @Mutable
    public static TagType<CompoundTag> TYPE;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void coo$installFastLoad(CallbackInfo ci) {
        NbtFastLoad.compoundFactory = CompoundTagInvoker::coo$create;
        TYPE = NbtFastLoad.compoundType(TYPE);
    }
}
