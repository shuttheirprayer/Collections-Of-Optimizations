package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Method;

public final class IdleEntities {

    public interface State {
        Vec3 coo$idlePos();

        Vec3 coo$idleMotion();

        BlockState coo$idleFeet();

        BlockState coo$idleBelow();

        void coo$sleep(Vec3 pos, Vec3 motion, BlockState feet, BlockState below);

        boolean coo$skipTick();


        boolean coo$mergeable();

        void coo$wake();
    }

    private static final ClassValue<Boolean> PLAIN_FLOOR = new ClassValue<>() {
        @Override
        protected Boolean computeValue(Class<?> type) {
            try {
                String stepOn = ObfuscationReflectionHelper.findMethod(Block.class, "m_141947_", Level.class, BlockPos.class, BlockState.class, Entity.class).getName();
                String fallOn = ObfuscationReflectionHelper.findMethod(Block.class, "m_5548_", BlockGetter.class, Entity.class).getName();
                for (Class<?> c = type; c != null && c != Block.class; c = c.getSuperclass()) {
                    if (declares(c, stepOn, Level.class, BlockPos.class, BlockState.class, Entity.class) || declares(c, fallOn, BlockGetter.class, Entity.class)) {
                        return false;
                    }
                }
                return true;
            } catch (Throwable t) {
                return false;
            }
        }
    };

    private static final ClassValue<Boolean> ITEM_UPDATE_HOOK = new ClassValue<>() {
        @Override
        protected Boolean computeValue(Class<?> type) {
            for (Class<?> c = type; c != null && c != Object.class; c = c.getSuperclass()) {
                if (c != Item.class && declares(c, "onEntityItemUpdate", ItemStack.class, ItemEntity.class)) {
                    return true;
                }
            }
            return false;
        }
    };

    private IdleEntities() {
    }

    private static boolean declares(Class<?> type, String name, Class<?>... params) {
        try {
            Method method = type.getDeclaredMethod(name, params);
            return !method.isBridge() && !method.isSynthetic();
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    public static boolean asleep(Entity entity) {
        return ((State) entity).coo$idleFeet() != null;
    }

    public static boolean itemHasUpdateHook(Item item) {
        return ITEM_UPDATE_HOOK.get(item.getClass());
    }

    public static boolean stillIdle(Entity entity) {
        State state = (State) entity;
        if (entity.position() != state.coo$idlePos() || entity.getDeltaMovement() != state.coo$idleMotion()) {
            return false;
        }
        return entity.level().getBlockState(entity.blockPosition()) == state.coo$idleFeet();
    }

    public static boolean settled(Entity entity, boolean supported, boolean nothingInside) {
        Level level = entity.level();
        AABB box = entity.getBoundingBox();
        if (entity.isOnFire() || level.containsAnyLiquid(box)) {
            return false;
        }
        if (level.getBlockState(entity.getOnPos()) != ((State) entity).coo$idleBelow()) {
            return false;
        }
        if (supported && level.noCollision(entity, box.move(0.0, -1.0E-3, 0.0))) {
            return false;
        }
        return !nothingInside || level.noCollision(entity, box.deflate(1.0E-7));
    }

    public static void trySleep(Entity entity, boolean gravity, boolean nothingInside) {
        Level level = entity.level();
        if (level.isClientSide || entity.isRemoved() || entity.isPassenger() || entity.isVehicle() || entity.isOnFire()) {
            return;
        }
        Vec3 motion = entity.getDeltaMovement();
        if (gravity) {
            if (!entity.onGround() || motion.horizontalDistanceSqr() > 1.0E-5 || motion.y > 0.1 || motion.y < -0.2) {
                return;
            }
        } else if (motion.lengthSqr() > 1.0E-5) {
            return;
        }
        BlockState feet = level.getBlockState(entity.blockPosition());
        BlockState below = level.getBlockState(entity.getOnPos());
        if (gravity && (!feet.isAir() || !PLAIN_FLOOR.get(below.getBlock().getClass()))) {
            return;
        }
        AABB box = entity.getBoundingBox();
        if (level.containsAnyLiquid(box)) {
            return;
        }
        if (gravity && level.noCollision(entity, box.move(0.0, -1.0E-3, 0.0))) {
            return;
        }
        if (nothingInside && !level.noCollision(entity, box.deflate(1.0E-7))) {
            return;
        }
        ((State) entity).coo$sleep(entity.position(), motion, feet, below);
    }
}
