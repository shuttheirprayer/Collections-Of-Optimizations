package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.RenderLayerMemo;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(value = ItemBlockRenderTypes.class, priority = 1100)
public abstract class MixinItemBlockRenderTypesMemo {

    @Shadow(remap = false) @Final
    private static Map<Holder.Reference<Block>, ChunkRenderTypeSet> BLOCK_RENDER_TYPES;

    @Inject(method = "getRenderLayers", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void coo$memoRenderLayers(BlockState state, CallbackInfoReturnable<ChunkRenderTypeSet> cir) {
        if (!CoOConfig.forgeRenderLayerMemo) {
            return;
        }
        Block block = state.getBlock();
        if (block instanceof LeavesBlock) {
            return;
        }
        RenderLayerMemo.Holder holder = (RenderLayerMemo.Holder) block;
        int version = RenderLayerMemo.version;
        RenderLayerMemo memo = holder.coo$renderLayerMemo();
        if (memo != null && memo.stamp == version) {
            cir.setReturnValue(memo.layers);
            return;
        }
        ChunkRenderTypeSet layers = BLOCK_RENDER_TYPES.get(ForgeRegistries.BLOCKS.getDelegateOrThrow(block));
        holder.coo$setRenderLayerMemo(new RenderLayerMemo(layers, version));
        cir.setReturnValue(layers);
    }

    @Inject(
            method = "setRenderLayer(Lnet/minecraft/world/level/block/Block;Lnet/minecraftforge/client/ChunkRenderTypeSet;)V",
            at = {@At("HEAD"), @At("RETURN")},
            remap = false,
            require = 0
    )
    private static void coo$invalidateRenderLayers(Block block, ChunkRenderTypeSet layers, CallbackInfo ci) {
        RenderLayerMemo.version++;
    }
}
