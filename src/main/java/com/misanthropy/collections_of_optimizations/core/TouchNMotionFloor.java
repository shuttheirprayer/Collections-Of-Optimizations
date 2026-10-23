package com.misanthropy.collections_of_optimizations.core;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import strm.touchnmotion.blockuse.ModBlock;
import strm.touchnmotion.create.ejector.EjectorLid;

public final class TouchNMotionFloor {

    private static final Vec3[] NORMALS = new Vec3[Direction.values().length];
    private static final MethodHandle NEW_FLOOR;
    private static final ModBlock EJECTOR;
    private static final MethodHandle STOCK_FLOOR;

    static {
        for (Direction direction : Direction.values()) NORMALS[direction.ordinal()] = Vec3.atLowerCornerOf(direction.getNormal());
        try {
            Class<?> floor = Class.forName("strm.touchnmotion.footgrounding.compat.TerrainBalance$Floor");
            NEW_FLOOR = MethodHandles.privateLookupIn(floor, MethodHandles.lookup())
                    .findConstructor(floor, MethodType.methodType(void.class, Vec3.class, Vec3.class, double.class))
                    .asType(MethodType.methodType(Object.class, Vec3.class, Vec3.class, double.class));
            Class<?> balance = Class.forName("strm.touchnmotion.footgrounding.compat.TerrainBalance");
            STOCK_FLOOR = MethodHandles.privateLookupIn(balance, MethodHandles.lookup())
                    .findStatic(balance, "floor", MethodType.methodType(floor, net.minecraft.client.player.AbstractClientPlayer.class, Vec3.class, java.util.List.class))
                    .asType(MethodType.methodType(Object.class, net.minecraft.client.player.AbstractClientPlayer.class, Vec3.class, java.util.List.class));
            Field block = EjectorLid.class.getDeclaredField("BLOCK");
            block.setAccessible(true);
            EJECTOR = (ModBlock) block.get(null);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Touch'n Motion's floor search is not as expected", e);
        }
    }

    private TouchNMotionFloor() {}

    public static Object stock(net.minecraft.client.player.AbstractClientPlayer player, Vec3 foot, java.util.List<?> spaces) throws Throwable {
        return STOCK_FLOOR.invokeExact(player, foot, spaces);
    }

    public static @Nullable Object floor(Entity player, Vec3 foot) throws Throwable {
        Level level = player.level();
        double x = foot.x + 0.0;
        double z = foot.z + 0.0;
        double startY = foot.y + 0.7;
        double endY = foot.y + -0.8;
        LeanRay ray = LeanRay.get();
        if (!ray.cast(level, null, ray.contextFor(player), true, x, startY, z, x, endY, z) || ray.inside) return null;
        Vec3 normal = NORMALS[ray.direction.ordinal()];
        BlockState state = ray.hitState;
        Vec3 point = EJECTOR.is(state) ? EjectorLid.onLid(player, ray.result()) : new Vec3(ray.hitX, ray.hitY, ray.hitZ);
        if (normal.y < 0.5 || point.y >= startY - 0.01) return null;
        double supportY = point.y;
        if (state.getBlock() instanceof FenceBlock || state.getBlock() instanceof WallBlock || state.getBlock() instanceof FenceGateBlock) {
            BlockPos pos = ray.blockPos();
            BlockHitResult visible = state.getShape(level, pos).clip(new Vec3(x, startY, z), new Vec3(x, endY, z), pos);
            if (visible != null && !visible.isInside()) point = visible.getLocation();
        }
        return NEW_FLOOR.invokeExact(point, normal, supportY);
    }
}
