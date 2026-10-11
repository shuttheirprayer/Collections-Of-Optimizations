package com.misanthropy.collections_of_optimizations.mixin.create;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ContraptionCollisionUnion;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.ContraptionWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

@Mixin(value = Contraption.class, remap = false)
public abstract class MixinContraptionCollisionUnion {

    @Shadow
    protected Map<BlockPos, StructureTemplate.StructureBlockInfo> blocks;

    @Shadow
    protected ContraptionWorld collisionLevel;

    @Inject(
            method = "lambda$gatherBBsOffThread$24()Ljava/util/List;",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void coo$singlePassCollisionBoxes(CallbackInfoReturnable<List<AABB>> cir) {
        if (CoOConfig.createSinglePassContraptionCollision) {
            cir.setReturnValue(ContraptionCollisionUnion.collisionBoxes(this.blocks, (BlockGetter) (Object) this.collisionLevel));
        }
    }
}
