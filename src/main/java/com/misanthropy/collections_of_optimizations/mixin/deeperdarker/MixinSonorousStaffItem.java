package com.misanthropy.collections_of_optimizations.mixin.deeperdarker;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.kyanite.deeperdarker.content.items.SonorousStaffItem", remap = false)
public abstract class MixinSonorousStaffItem {

    @Inject(
            method = "m_6883_(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;IZ)V",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void coo$leanChargeTag(ItemStack stack, Level level, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (!CoOConfig.deeperdarkerLeanStaffChargeTag) {
            return;
        }
        ci.cancel();
        if (!(entity instanceof Player player)) {
            return;
        }
        boolean charged = player.getUseItem() == stack && stack.getUseDuration() - player.getUseItemRemainingTicks() >= 123;
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            if (charged) {
                stack.getOrCreateTag().putBoolean("charged", true);
            }
        } else if (tag.getBoolean("charged") != charged) {
            tag.putBoolean("charged", charged);
        }
    }
}
