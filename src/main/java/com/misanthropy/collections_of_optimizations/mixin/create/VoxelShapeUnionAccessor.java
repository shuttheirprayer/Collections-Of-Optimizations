package com.misanthropy.collections_of_optimizations.mixin.create;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(VoxelShape.class)
public interface VoxelShapeUnionAccessor {

    @Accessor("shape")
    DiscreteVoxelShape coo$getShape();

    @Invoker("getCoords")
    DoubleList coo$getCoords(Direction.Axis axis);
}
