package com.misanthropy.collections_of_optimizations.mixin.ribbits;

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
@Mixin(targets = "com.yungnickyoung.minecraft.ribbits.entity.goal.RibbitPlayMusicGoal", remap = false)
public abstract class MixinRibbitsBandScan {

    @Unique
    private List<Entity> coo$bandScanCache;

    @Unique
    private int coo$bandScanCooldown;

    @WrapOperation(
            method = "m_8036_",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;m_45976_(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"
            ),
            require = 0
    )
    private List<Entity> coo$leanBandScanCanUse(Level level, Class<Entity> type, AABB box, Operation<List<Entity>> original) {
        return this.coo$bandScan(level, type, box, original);
    }

    @WrapOperation(
            method = "m_8037_",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;m_45976_(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"
            ),
            require = 0
    )
    private List<Entity> coo$leanBandScanTick(Level level, Class<Entity> type, AABB box, Operation<List<Entity>> original) {
        return this.coo$bandScan(level, type, box, original);
    }

    @Unique
    private List<Entity> coo$bandScan(Level level, Class<Entity> type, AABB box, Operation<List<Entity>> original) {
        int interval = CoOConfig.ribbitsBandScanInterval;
        if (interval <= 1) {
            this.coo$bandScanCache = null;
            this.coo$bandScanCooldown = 0;
            return original.call(level, type, box);
        }
        List<Entity> cached = this.coo$bandScanCache;
        if (cached != null && this.coo$bandScanCooldown > 0) {
            boolean usable = true;
            for (int i = 0; i < cached.size(); i++) {
                if (cached.get(i).isRemoved()) {
                    usable = false;
                    break;
                }
            }
            if (usable) {
                this.coo$bandScanCooldown--;
                return cached;
            }
        }
        List<Entity> fresh = original.call(level, type, box);
        this.coo$bandScanCache = fresh;
        this.coo$bandScanCooldown = interval;
        return fresh;
    }
}
