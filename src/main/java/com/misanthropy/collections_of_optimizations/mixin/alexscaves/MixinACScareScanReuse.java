package com.misanthropy.collections_of_optimizations.mixin.alexscaves;

import com.github.alexmodguy.alexscaves.server.entity.living.LuxtructosaurusEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.TremorsaurusEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.TremorzillaEntity;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(value = {TremorsaurusEntity.class, TremorzillaEntity.class, LuxtructosaurusEntity.class}, remap = false)
public abstract class MixinACScareScanReuse {

    @Shadow(remap = false)
    private int lastScareTimestamp;

    @Unique
    private List<Entity> coo$scareList;

    @Unique
    private int coo$scareScanTick = Integer.MIN_VALUE;

    @WrapOperation(
            method = "scareMobs",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"),
            require = 0
    )
    private List<Entity> coo$reuseScan(Level level, Class<Entity> type, AABB area, Operation<List<Entity>> original) {
        int now = ((Entity) (Object) this).tickCount;
        List<Entity> list = this.coo$scareList;
        if (!CoOConfig.alexscavesScareScanReuse || list == null || now - this.coo$scareScanTick > 3 || now < this.coo$scareScanTick
                || now == this.lastScareTimestamp) {
            list = original.call(level, type, area);
            this.coo$scareList = list;
            this.coo$scareScanTick = now;
        } else {
            list.removeIf(Entity::isRemoved);
        }
        return list;
    }
}
