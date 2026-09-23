package com.misanthropy.collections_of_optimizations.core;

import com.github.L_Ender.lionfishapi.server.event.StandOnFluidEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.ListenerList;

public final class LionfishFluidWalk {

    private static final int[] OFFSETS = {
            0, 0, 0,
            0, -1, 0,
            0, 0, -1,
            -1, 0, 0,
            -1, -1, 0,
            -1, 0, -1
    };

    private static final ListenerList LISTENERS = LionfishEventProbe.listOf(StandOnFluidEvent.class);

    private LionfishFluidWalk() {
    }

    public static Vec3 collide(Entity self, Vec3 original) {
        if (!(self instanceof LivingEntity entity) || original.y > 0.0 || !LionfishEventProbe.hasListeners(LISTENERS)) {
            return original;
        }
        Level level = entity.level();
        BlockPos source = entity.blockPosition();
        int sourceX = source.getX();
        int sourceY = source.getY();
        int sourceZ = source.getZ();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        VoxelShape entityShape = null;
        double highestValue = original.y;
        FluidState highestFluid = null;
        for (int i = 0; i < OFFSETS.length; i += 3) {
            pos.set(sourceX + OFFSETS[i], sourceY + OFFSETS[i + 1], sourceZ + OFFSETS[i + 2]);
            FluidState fluidState = level.getFluidState(pos);
            if (fluidState.isEmpty()) {
                continue;
            }
            VoxelShape shape = Shapes.block().move(pos.getX(), (float) pos.getY() + fluidState.getOwnHeight(), pos.getZ());
            if (entityShape == null) {
                entityShape = Shapes.create(entity.getBoundingBox().inflate(0.5));
            }
            if (Shapes.joinIsNotEmpty(shape, entityShape, BooleanOp.AND)) {
                double height = shape.max(Direction.Axis.Y) - entity.getY() - 1.0;
                if (highestValue < height) {
                    highestValue = height;
                    highestFluid = fluidState;
                }
            }
        }
        if (highestFluid == null) {
            return original;
        }
        StandOnFluidEvent event = new StandOnFluidEvent(entity, highestFluid);
        MinecraftForge.EVENT_BUS.post(event);
        if (!event.isCanceled()) {
            return original;
        }
        entity.fallDistance = 0.0F;
        entity.setOnGround(true);
        return new Vec3(original.x, highestValue, original.z);
    }
}
