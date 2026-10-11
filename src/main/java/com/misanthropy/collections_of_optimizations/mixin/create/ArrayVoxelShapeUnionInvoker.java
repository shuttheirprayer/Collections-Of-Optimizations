package com.misanthropy.collections_of_optimizations.mixin.create;

import net.minecraft.world.phys.shapes.ArrayVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ArrayVoxelShape.class)
public interface ArrayVoxelShapeUnionInvoker {

    @Invoker("<init>")
    static ArrayVoxelShape coo$create(DiscreteVoxelShape shape, double[] xs, double[] ys, double[] zs) {
        throw new AssertionError();
    }
}
