package com.misanthropy.collections_of_optimizations.mixin.nbtload;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(CompoundTag.class)
public interface CompoundTagInvoker {

    @Invoker("<init>")
    static CompoundTag coo$create(Map<String, Tag> tags) {
        throw new AssertionError();
    }
}
