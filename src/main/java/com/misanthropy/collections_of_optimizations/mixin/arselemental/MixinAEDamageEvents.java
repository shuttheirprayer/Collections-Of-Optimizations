package com.misanthropy.collections_of_optimizations.mixin.arselemental;

import alexthw.ars_elemental.ConfigHandler;
import alexthw.ars_elemental.api.item.ISchoolBangle;
import alexthw.ars_elemental.api.item.ISchoolFocus;
import alexthw.ars_elemental.event.DamageEvents;
import alexthw.ars_elemental.registry.ModPotions;
import com.hollingsworth.arsnouveau.api.spell.SpellSchools;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.CurioPresenceCache;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DamageEvents.class, remap = false)
public class MixinAEDamageEvents {

    @Inject(method = "handleHealing", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$fixGlobalHealBoost(LivingHealEvent event, CallbackInfo ci) {
        if (!CoOConfig.arselementalFixGlobalHealBoost) {
            return;
        }
        LivingEntity entity = event.getEntity();
        if (entity == null) {
            return;
        }

        if (ConfigHandler.COMMON.EnableGlyphEmpowering.get()
                && entity instanceof Player player
                && ISchoolFocus.hasFocus(player) == SpellSchools.ELEMENTAL_EARTH) {
            event.setAmount(event.getAmount() * 1.5F);
        }

        if (entity.hasEffect(ModPotions.FROZEN.get())) {
            event.setCanceled(true);
        }

        if (entity.hasEffect(ModPotions.MAGIC_FIRE.get()) && ConfigHandler.COMMON.IFRAME_SKIP.get()) {
            event.setAmount((float) (event.getAmount() * 1.25));
            entity.invulnerableTime = 0;
        }

        ci.cancel();
    }

    @Inject(method = "banglesSpecials", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$skipBangleScan(LivingAttackEvent event, CallbackInfo ci) {
        if (!CoOConfig.arselementalSkipBangleScan) {
            return;
        }
        LivingEntity victim = event.getEntity();
        if (victim == null) {
            return;
        }

        Entity source = event.getSource().getEntity();
        if (source instanceof Player attacker && victim != attacker
                && !Boolean.FALSE.equals(CurioPresenceCache.equippedInstanceOf(attacker, ISchoolBangle.class))) {
            return;
        }

        if (victim instanceof Player defender
                && !Boolean.FALSE.equals(CurioPresenceCache.equippedInstanceOf(defender, ISchoolBangle.class))) {
            return;
        }

        ci.cancel();
    }
}
