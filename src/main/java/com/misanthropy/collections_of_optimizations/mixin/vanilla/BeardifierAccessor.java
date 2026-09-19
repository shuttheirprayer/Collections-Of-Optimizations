package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.structure.pools.JigsawJunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Beardifier.class)
public interface BeardifierAccessor {

    @Accessor("pieceIterator")
    ObjectListIterator<Beardifier.Rigid> coo$pieceIterator();

    @Accessor("junctionIterator")
    ObjectListIterator<JigsawJunction> coo$junctionIterator();
}
