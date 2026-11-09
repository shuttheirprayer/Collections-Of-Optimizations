package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class SkarrierScans {

    public static List<Entity> playersIn(LevelAccessor level, AABB box, Predicate<? super Entity> filter) {
        List<Entity> hits = null;
        for (Player player : level.players()) {
            if (player.getBoundingBox().intersects(box) && filter.test(player)) {
                if (hits == null) {
                    hits = new ArrayList<>(2);
                }
                hits.add(player);
            }
        }
        return hits == null ? List.of() : hits;
    }

    public static List<Entity> targetIn(Entity self, AABB box, Predicate<? super Entity> filter) {
        if (!(self instanceof Mob mob)) {
            return List.of();
        }
        LivingEntity target = mob.getTarget();
        if (target == null || !target.getBoundingBox().intersects(box) || !filter.test(target)) {
            return List.of();
        }
        return List.of(target);
    }

    public static List<Entity> ownerIn(Entity self, AABB box, Predicate<? super Entity> filter) {
        if (!(self instanceof TamableAnimal tamable)) {
            return List.of();
        }
        LivingEntity owner = tamable.getOwner();
        if (owner == null || !owner.getBoundingBox().intersects(box) || !filter.test(owner)) {
            return List.of();
        }
        return List.of(owner);
    }

    public static Predicate<Entity> tagged(TagKey<EntityType<?>> tag, Predicate<? super Entity> filter) {
        return entity -> entity.getType().is(tag) && filter.test(entity);
    }

    private SkarrierScans() {
    }
}
