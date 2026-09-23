package com.misanthropy.collections_of_optimizations.mixin.l2backpack;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "dev.xkmc.l2backpack.content.common.BaseBagItem", remap = false)
public abstract class MixinL2BackpackBagListTag {

    @Inject(
            method = "getListTag(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/nbt/ListTag;",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$readListTagWithoutCreating(ItemStack stack, CallbackInfoReturnable<ListTag> cir) {
        if (!CoOConfig.l2backpackLeanBagTagRead || stack == null) {
            return;
        }

        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains("Items")) {
            cir.setReturnValue(new ListTag());
            return;
        }

        cir.setReturnValue(tag.getList("Items", Tag.TAG_COMPOUND));
    }
}
