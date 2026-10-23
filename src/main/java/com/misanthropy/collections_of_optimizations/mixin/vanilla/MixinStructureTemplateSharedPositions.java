package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.TemplatePositions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(StructureTemplate.class)
public abstract class MixinStructureTemplateSharedPositions {

    @Redirect(method = "loadPalette", at = @At(value = "NEW", target = "(III)Lnet/minecraft/core/BlockPos;"), require = 0)
    private BlockPos coo$sharedPosition(int x, int y, int z) {
        return CoOConfig.vanillaShareTemplatePositions ? TemplatePositions.of(x, y, z) : new BlockPos(x, y, z);
    }
}
