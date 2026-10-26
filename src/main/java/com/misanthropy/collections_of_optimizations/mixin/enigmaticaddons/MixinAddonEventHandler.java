package com.misanthropy.collections_of_optimizations.mixin.enigmaticaddons;

import auviotre.enigmatic.addon.handlers.AddonEventHandler;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.mixin.vanilla.EntityPersistentDataAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AddonEventHandler.class, remap = false)
public abstract class MixinAddonEventHandler {

    @Inject(method = "onTick", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$skipUnsetPersistentData(LivingEvent.LivingTickEvent event, CallbackInfo ci) {
        if (!CoOConfig.enigmaticaddonsSkipUnsetPersistentData) {
            return;
        }
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player || entity instanceof EnderDragon || entity instanceof WitherSkeleton
                || entity instanceof Phantom || entity instanceof OwnableEntity) {
            return;
        }
        if (entity.getTicksFrozen() != 0) {
            return;
        }
        if (entity instanceof EntityPersistentDataAccessor access && coo$idle(access.coo$persistentData())) {
            ci.cancel();
        }
    }

    @Unique
    private static boolean coo$idle(CompoundTag tag) {
        return tag == null || (!tag.contains("ForgottenFrozenHard")
                && !tag.contains("RevivingPoisoned")
                && !tag.contains("CosmicPotion")
                && !tag.contains("Ichor")
                && !tag.contains("Electric")
                && !tag.contains("CursedNextSpawn")
                && !tag.contains("BlessNextSpawn")
                && !tag.contains("WorthyNextSpawn")
                && !tag.contains("EvilCurseThreshold")
                && !tag.contains("DisasterCurse"));
    }

    @Redirect(
            method = "onTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;m_44836_(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/entity/LivingEntity;)I",
                    remap = false
            ),
            require = 0
    )
    private int coo$skipIdleFrostProtectionScan(Enchantment enchantment, LivingEntity entity) {
        if (CoOConfig.enigmaticaddonsSkipIdleFrostScan && entity.getTicksFrozen() == 0) {
            return 0;
        }
        return EnchantmentHelper.getEnchantmentLevel(enchantment, entity);
    }
}
