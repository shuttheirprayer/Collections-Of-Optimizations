package com.misanthropy.collections_of_optimizations.mixin.netherexp;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.jadenxgamer.netherexp.registry.item.custom.SanctumCompassItem;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = SanctumCompassItem.class, remap = false)
public abstract class MixinJneSanctumCompass {

    @Inject(method = "m_6883_", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$skipIdleCompassTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (!CoOConfig.netherexpLeanSanctumCompassTick) {
            return;
        }
        if (level.isClientSide) {
            ci.cancel();
            return;
        }
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.getBoolean("IsActive")) {
            ci.cancel();
        }
    }

    @Inject(method = "m_7373_", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$leanCompassTooltip(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag, CallbackInfo ci) {
        if (!CoOConfig.netherexpLeanSanctumCompassTooltip || stack.getTag() != null) {
            return;
        }
        tooltip.add(Component.empty());
        tooltip.add(Component.translatable("sanctum_compass.to_activate").withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("item.netherexp.wraithing_flesh").withStyle(ChatFormatting.DARK_PURPLE));
        ci.cancel();
    }
}
