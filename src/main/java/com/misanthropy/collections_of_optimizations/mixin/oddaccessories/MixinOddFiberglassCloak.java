package com.misanthropy.collections_of_optimizations.mixin.oddaccessories;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.oddaccessories.item.FiberglassCloakItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collections;
import java.util.List;

@Mixin(value = FiberglassCloakItem.class, remap = false)
public abstract class MixinOddFiberglassCloak {

    @WrapOperation(
            method = "onCurioTick",
            at = @At(
                    value = "INVOKE",
                    ordinal = 1,
                    target = "Lnet/minecraft/world/level/Level;m_45976_(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"
            ),
            require = 0
    )
    private List<Mob> coo$throttleCloakAggroScan(Level level, Class<Mob> type, AABB box, Operation<List<Mob>> original) {
        int interval = CoOConfig.oddaccessoriesCloakAggroScanInterval;
        if (interval > 1 && level.getGameTime() % interval != 0L) {
            return Collections.emptyList();
        }
        return original.call(level, type, box);
    }

    @WrapWithCondition(
            method = "onCurioTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;m_5661_(Lnet/minecraft/network/chat/Component;Z)V"
            ),
            require = 0
    )
    private boolean coo$throttleCloakActionBar(Player player, Component message, boolean actionBar) {
        int interval = CoOConfig.oddaccessoriesCloakActionBarInterval;
        return interval <= 1 || player.level().getGameTime() % interval == 0L;
    }
}
