package com.misanthropy.collections_of_optimizations.mixin.radium;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.EntityGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntitySectionStorage;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.entity.PartEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Pseudo
@Mixin(targets = "me.jellysquid.mods.lithium.common.world.WorldHelper", remap = false)
public abstract class MixinRadiumPartEntityCollisions {

    @WrapOperation(
            method = "getEntitiesForCollision(Lnet/minecraft/world/level/EntityGetter;Lnet/minecraft/world/phys/AABB;Lnet/minecraft/world/entity/Entity;)Ljava/util/List;",
            at = @At(
                    value = "INVOKE",
                    target = "Lme/jellysquid/mods/lithium/common/world/WorldHelper;getEntitiesOfClassGroup(Lnet/minecraft/world/level/entity/EntitySectionStorage;Lnet/minecraft/world/entity/Entity;Lme/jellysquid/mods/lithium/common/entity/EntityClassGroup$NoDragonClassGroup;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"
            ),
            require = 0
    )
    private static List<Entity> coo$appendPartEntities(EntitySectionStorage<Entity> cache,
                                                       Entity colliding,
                                                       @Coerce Object group,
                                                       AABB box,
                                                       Operation<List<Entity>> original,
                                                       @Local(argsOnly = true) EntityGetter view) {
        List<Entity> entities = original.call(cache, colliding, group, box);
        if (!CoOConfig.radiumPartEntityCollisions || !(view instanceof Level level)) {
            return entities;
        }
        Collection<PartEntity<?>> parts = level.getPartEntities();
        if (parts.isEmpty()) {
            return entities;
        }
        List<Entity> result = entities;
        for (PartEntity<?> part : parts) {
            if (part != colliding && !part.isSpectator() && part.getBoundingBox().intersects(box)) {
                if (result == entities && !(entities instanceof ArrayList)) {
                    result = new ArrayList<>(entities);
                }
                result.add(part);
            }
        }
        return result;
    }
}
