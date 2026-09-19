package com.misanthropy.collections_of_optimizations.mixin.cnb;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Pseudo
@Mixin(targets = "com.cgessinger.creaturesandbeasts.entities.CactemEntity$HealGoal", remap = false)
public abstract class MixinCnbCactemHealScan {

    @Unique
    private List<Entity> coo$healScanCache;

    @Unique
    private int coo$healScanCooldown;

    @WrapOperation(
            method = "cactemNeedsHeal",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;m_45976_(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"
            ),
            require = 0
    )
    private List<Entity> coo$leanHealScan(Level level, Class<Entity> type, AABB box, Operation<List<Entity>> original) {
        int interval = CoOConfig.cnbCactemHealScanInterval;
        if (interval <= 1) {
            this.coo$healScanCache = null;
            this.coo$healScanCooldown = 0;
            return original.call(level, type, box);
        }
        List<Entity> cached = this.coo$healScanCache;
        if (cached != null && this.coo$healScanCooldown > 0) {
            boolean usable = true;
            for (int i = 0; i < cached.size(); i++) {
                if (cached.get(i).isRemoved()) {
                    usable = false;
                    break;
                }
            }
            if (usable) {
                this.coo$healScanCooldown--;
                return cached;
            }
        }
        List<Entity> fresh = original.call(level, type, box);
        this.coo$healScanCache = fresh;
        this.coo$healScanCooldown = interval;
        return fresh;
    }
}
