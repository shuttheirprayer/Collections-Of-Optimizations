package com.misanthropy.collections_of_optimizations.mixin.mekanism;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;

@Pseudo
@Mixin(targets = "mekanism.common.tile.transmitter.TileEntityTransmitter", remap = false)
public abstract class MixinTransmitterRenderBox extends BlockEntity {

    @Unique
    private AABB coo$renderBox;

    private MixinTransmitterRenderBox(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Overwrite
    public AABB getRenderBoundingBox() {
        AABB box = this.coo$renderBox;
        if (box == null) {
            box = new AABB(this.worldPosition, this.worldPosition.offset(1, 1, 1));
            this.coo$renderBox = box;
        }
        return box;
    }
}
