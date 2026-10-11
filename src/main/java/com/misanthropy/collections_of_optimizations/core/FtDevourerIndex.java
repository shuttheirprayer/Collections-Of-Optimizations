package com.misanthropy.collections_of_optimizations.core;

import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import dev.xkmc.l2hostility.content.logic.TraitManager;
import dev.xkmc.l2hostility.content.traits.base.MobTrait;
import net.kayn.fallen_traits.init.FTConfig;
import net.kayn.fallen_traits.init.FTTraits;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.Iterator;
import java.util.Set;

public final class FtDevourerIndex {

    private FtDevourerIndex() {
    }

    public static void seen(LivingEntity mob, MobTraitCap cap) {
        if (!(mob instanceof FtEntityState state) || state.coo$ftDevourerListed()) {
            return;
        }
        Level level = mob.level();
        if (level.isClientSide() || !(level instanceof FtLevelState holder)) {
            return;
        }
        MobTrait devourer = FTTraits.DEVOURER.get();
        if (!cap.traits.containsKey(devourer)) {
            return;
        }
        state.coo$ftSetDevourerListed(true);
        holder.coo$ftDevourers().add(mob);
    }

    public static boolean mayAbsorb(LivingEntity target) {
        if (!(target.level() instanceof FtLevelState holder)) {
            return true;
        }
        Set<LivingEntity> devourers = holder.coo$ftDevourers();
        if (devourers.isEmpty()) {
            return false;
        }
        MobTrait devourer = FTTraits.DEVOURER.get();
        double maxRadius = (double) (FTConfig.COMMON.devourerRadiusPerLevel.get() * TraitManager.getMaxLevel());
        AABB box = target.getBoundingBox().inflate(maxRadius);
        boolean found = false;
        Iterator<LivingEntity> iterator = devourers.iterator();
        while (iterator.hasNext()) {
            LivingEntity entity = iterator.next();
            if (entity == null) {
                continue;
            }
            if (entity.isRemoved() || entity.level() != target.level() || !stillDevourer(entity, devourer)) {
                iterator.remove();
                if (entity instanceof FtEntityState state) {
                    state.coo$ftSetDevourerListed(false);
                }
                continue;
            }
            if (!found && entity != target && entity.getBoundingBox().intersects(box)) {
                found = true;
            }
        }
        return found;
    }

    private static boolean stillDevourer(LivingEntity entity, MobTrait devourer) {
        return MobTraitCap.HOLDER.isProper(entity) && MobTraitCap.HOLDER.get(entity).traits.containsKey(devourer);
    }
}
