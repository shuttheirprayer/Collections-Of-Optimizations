package com.misanthropy.collections_of_optimizations.mixin.nbtload;

import com.misanthropy.collections_of_optimizations.core.NbtFastLoad;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.TagType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ListTag.class, priority = 1500)
public abstract class MixinListTagFastLoad {

    @Shadow
    @Final
    @Mutable
    public static TagType<ListTag> TYPE;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void coo$installFastLoad(CallbackInfo ci) {
        NbtFastLoad.listFactory = ListTagInvoker::coo$create;
        TYPE = NbtFastLoad.listType(TYPE);
    }
}
