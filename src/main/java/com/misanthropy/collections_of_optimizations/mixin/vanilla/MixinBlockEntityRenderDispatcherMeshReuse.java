package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.core.EntityMeshReuse;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockEntityRenderDispatcher.class)
public abstract class MixinBlockEntityRenderDispatcherMeshReuse {

    @WrapMethod(method = "setupAndRender")
    private static void coo$meshReuse(BlockEntityRenderer<BlockEntity> renderer, BlockEntity blockEntity, float partialTick,
                                      PoseStack poseStack, MultiBufferSource buffers, Operation<Void> original) {
        EntityMeshReuse.renderBlockEntity(blockEntity, poseStack, buffers,
                target -> original.call(renderer, blockEntity, partialTick, poseStack, target));
    }
}
