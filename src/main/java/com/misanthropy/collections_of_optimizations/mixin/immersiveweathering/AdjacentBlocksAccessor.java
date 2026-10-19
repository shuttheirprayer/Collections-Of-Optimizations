package com.misanthropy.collections_of_optimizations.mixin.immersiveweathering;

import com.ordana.immersive_weathering.data.fluid_generators.SelfFluidGenerator;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = SelfFluidGenerator.AdjacentBlocks.class, remap = false)
public interface AdjacentBlocksAccessor {

    @Accessor("anyBlocks")
    List<RuleTest> coo$getAnyBlocks();

    @Accessor("sidesBlocks")
    List<RuleTest> coo$getSidesBlocks();

    @Accessor("upBlock")
    RuleTest coo$getUpBlock();

    @Accessor("downBlock")
    RuleTest coo$getDownBlock();
}
