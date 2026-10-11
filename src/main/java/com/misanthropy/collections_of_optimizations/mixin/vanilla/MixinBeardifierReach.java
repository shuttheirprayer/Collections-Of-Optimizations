package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.BeardifierReach;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.structure.pools.JigsawJunction;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Beardifier.class)
public abstract class MixinBeardifierReach {

    @Shadow
    @Final
    protected ObjectListIterator<Beardifier.Rigid> pieceIterator;

    @Shadow
    @Final
    protected ObjectListIterator<JigsawJunction> junctionIterator;

    @Unique
    private BeardifierReach coo$reach;

    @ModifyExpressionValue(
            method = "compute",
            at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/world/level/levelgen/Beardifier;pieceIterator:Lit/unimi/dsi/fastutil/objects/ObjectListIterator;"),
            require = 0
    )
    private ObjectListIterator<Beardifier.Rigid> coo$piecesInReach(ObjectListIterator<Beardifier.Rigid> all,
                                                                   @Local(ordinal = 0) int x,
                                                                   @Local(ordinal = 1) int y,
                                                                   @Local(ordinal = 2) int z) {
        BeardifierReach reach = this.coo$reach();
        return reach == BeardifierReach.NONE ? all : reach.pieces(x, y, z);
    }

    @ModifyExpressionValue(
            method = "compute",
            at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/world/level/levelgen/Beardifier;junctionIterator:Lit/unimi/dsi/fastutil/objects/ObjectListIterator;"),
            require = 0
    )
    private ObjectListIterator<JigsawJunction> coo$junctionsInReach(ObjectListIterator<JigsawJunction> all,
                                                                    @Local(ordinal = 0) int x,
                                                                    @Local(ordinal = 1) int y,
                                                                    @Local(ordinal = 2) int z) {
        BeardifierReach reach = this.coo$reach();
        return reach == BeardifierReach.NONE ? all : reach.junctions(x, y, z);
    }

    @Unique
    private BeardifierReach coo$reach() {
        BeardifierReach reach = this.coo$reach;
        if (reach == null) {
            reach = CoOConfig.vanillaBoundBeardifierReach
                    ? BeardifierReach.plan(this, this.pieceIterator, this.junctionIterator)
                    : BeardifierReach.NONE;
            this.coo$reach = reach;
        }
        return reach;
    }
}
