package com.misanthropy.collections_of_optimizations.mixin.nbtload;

import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(ListTag.class)
public interface ListTagInvoker {

    @Invoker("<init>")
    static ListTag coo$create(List<Tag> list, byte type) {
        throw new AssertionError();
    }
}
