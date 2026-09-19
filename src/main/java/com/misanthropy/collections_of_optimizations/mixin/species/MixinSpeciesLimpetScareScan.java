package com.misanthropy.collections_of_optimizations.mixin.species;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

@Pseudo
@Mixin(targets = "com.ninni.species.server.entity.mob.update_1.Limpet", remap = false)
public abstract class MixinSpeciesLimpetScareScan {

    @Unique
    private int coo$scareScanCooldown;

    @WrapOperation(
            method = "m_8107_",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;m_6443_(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;"
            ),
            require = 0
    )
    private List<Player> coo$leanScareScan(Level level, Class<Player> type, AABB box, Predicate<? super Player> filter, Operation<List<Player>> original) {
        int interval = CoOConfig.speciesLimpetScareScanInterval;
        if (interval <= 1) {
            this.coo$scareScanCooldown = 0;
            return original.call(level, type, box, filter);
        }
        if (this.coo$scareScanCooldown > 0) {
            this.coo$scareScanCooldown--;
            return Collections.emptyList();
        }
        this.coo$scareScanCooldown = interval - 1;
        return original.call(level, type, box, filter);
    }
}
