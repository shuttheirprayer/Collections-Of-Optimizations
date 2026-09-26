package com.misanthropy.collections_of_optimizations.mixin.bowenchants;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "com.doug.bowenchants.events.BowEnchantsEvents", remap = false)
public abstract class MixinBowEnchantsStrictEnhancedPower {

    @WrapOperation(
            method = "onEntityJoinLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;m_44843_(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/item/ItemStack;)I"
            ),
            require = 0
    )
    private int coo$strictEnhancedPower(Enchantment enchantment, ItemStack stack, Operation<Integer> original, @Local(argsOnly = true) EntityJoinLevelEvent event) {
        int level = original.call(enchantment, stack);
        if (level <= 0 || !CoOConfig.bowenchantsStrictEnhancedPower) {
            return level;
        }
        Entity entity = event.getEntity();
        if (entity instanceof AbstractArrow) {
            return entity.getPersistentData().getBoolean("bowenchants.enhanced_power_arrow_applied") ? 0 : level;
        }
        if (entity instanceof Projectile projectile && projectile.getOwner() instanceof LivingEntity owner && !(owner.isUsingItem() && owner.getUseItem() == stack)) {
            return 0;
        }
        return level;
    }
}
