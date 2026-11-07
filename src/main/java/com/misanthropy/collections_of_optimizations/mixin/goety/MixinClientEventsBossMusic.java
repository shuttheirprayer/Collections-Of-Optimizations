package com.misanthropy.collections_of_optimizations.mixin.goety;

import com.Polarice3.Goety.common.entities.hostile.SkullLord;
import com.Polarice3.Goety.common.entities.hostile.WitherNecromancer;
import com.Polarice3.Goety.common.entities.hostile.cultists.Crone;
import com.Polarice3.Goety.common.entities.hostile.cultists.Heresiarch;
import com.Polarice3.Goety.client.events.ClientEvents;
import com.Polarice3.Goety.common.entities.boss.Apostle;
import com.Polarice3.Goety.common.entities.boss.EnderKeeper;
import com.Polarice3.Goety.common.entities.boss.Vizier;
import com.Polarice3.Goety.common.entities.hostile.Wight;
import com.Polarice3.Goety.common.entities.hostile.ender.Endersent;
import com.Polarice3.Goety.common.entities.hostile.illagers.HostileRedstoneGolem;
import com.Polarice3.Goety.common.entities.hostile.illagers.HostileRedstoneMonstrosity;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ClientEvents.class, remap = false)
public abstract class MixinClientEventsBossMusic {

    @WrapOperation(
            method = "onEntityTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/Polarice3/Goety/utils/MiscCapHelper;getMobTarget(Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/entity/Entity;"
            ),
            require = 0
    )
    private static Entity coo$skipBossMusicTargetLookup(LivingEntity livingEntity, Operation<Entity> original) {
        if (CoOConfig.goetySkipBossMusicTargetLookup && !coo$hasBossTheme(livingEntity)) {
            return null;
        }
        return original.call(livingEntity);
    }

    @Unique
    private static boolean coo$hasBossTheme(LivingEntity livingEntity) {
        return livingEntity instanceof Apostle
                || livingEntity instanceof Vizier
                || livingEntity instanceof HostileRedstoneMonstrosity
                || livingEntity instanceof EnderKeeper
                || livingEntity instanceof HostileRedstoneGolem
                || livingEntity instanceof Endersent
                || livingEntity instanceof Wight
                || livingEntity instanceof Crone
                || livingEntity instanceof Heresiarch
                || livingEntity instanceof SkullLord
                || livingEntity instanceof WitherNecromancer;
    }
}
