package com.misanthropy.collections_of_optimizations.mixin.l2complements;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "dev.xkmc.l2complements.events.ClientEventHandler", remap = false)
public abstract class MixinL2CTooltipDescriptions {

    @Inject(
            method = "modifyItemTooltip(Lnet/minecraftforge/event/entity/player/ItemTooltipEvent;)V",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$skipUnrelatedTooltip(ItemTooltipEvent event, CallbackInfo ci) {
        if (!CoOConfig.l2complementsSkipUnrelatedTooltips || event == null) {
            return;
        }

        ItemStack stack = event.getItemStack();
        if (stack == null || stack.isEmpty()) {
            ci.cancel();
            return;
        }

        CompoundTag tag = stack.getTag();
        if (tag == null) {
            ci.cancel();
            return;
        }

        if (!coo$hasOwnEnchantment(tag, "Enchantments") && !coo$hasOwnEnchantment(tag, "StoredEnchantments")) {
            ci.cancel();
        }
    }

    private static boolean coo$hasOwnEnchantment(CompoundTag tag, String key) {
        if (!tag.contains(key, Tag.TAG_LIST)) {
            return false;
        }
        ListTag list = tag.getList(key, Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            if (list.getCompound(i).getString("id").startsWith("l2complements:")) {
                return true;
            }
        }
        return false;
    }
}
