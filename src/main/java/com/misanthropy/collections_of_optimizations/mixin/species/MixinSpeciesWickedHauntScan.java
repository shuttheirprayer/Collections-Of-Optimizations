package com.misanthropy.collections_of_optimizations.mixin.species;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.Mob;
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
@Mixin(targets = "com.ninni.species.server.entity.mob.update_3.Wicked", remap = false)
public abstract class MixinSpeciesWickedHauntScan {

    @Unique
    private int coo$hauntScanCooldown;

    @WrapOperation(
            method = "m_8119_",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;m_6443_(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;"
            ),
            require = 0
    )
    private List<Mob> coo$leanHauntResyncScan(Level level, Class<Mob> type, AABB box, Predicate<? super Mob> filter, Operation<List<Mob>> original) {
        int interval = CoOConfig.speciesWickedHauntScanInterval;
        if (interval <= 1) {
            this.coo$hauntScanCooldown = 0;
            return original.call(level, type, box, filter);
        }
        if (this.coo$hauntScanCooldown > 0) {
            this.coo$hauntScanCooldown--;
            return Collections.emptyList();
        }
        this.coo$hauntScanCooldown = interval - 1;
        return original.call(level, type, box, filter);
    }
}
