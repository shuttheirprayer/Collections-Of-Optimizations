package com.misanthropy.collections_of_optimizations.mixin.cookingforblockheads;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.blay09.mods.cookingforblockheads.KitchenMultiBlock;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = KitchenMultiBlock.class, remap = false)
public abstract class MixinKitchenMultiBlock {

    @Shadow
    @Final
    private static List<Block> blockConnectors;

    @Inject(method = "registerConnectorBlock", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$dedupeConnector(Block block, CallbackInfo ci) {
        if (CoOConfig.cookingforblockheadsIdempotentCompatReload && blockConnectors.contains(block)) {
            ci.cancel();
        }
    }
}
