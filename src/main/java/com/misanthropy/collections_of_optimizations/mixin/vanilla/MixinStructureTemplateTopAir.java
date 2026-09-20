package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(value = StructureTemplateManager.class, priority = 1500)
public abstract class MixinStructureTemplateTopAir {

    @Inject(method = "loadFromResource", at = @At("RETURN"))
    private void coo$dropTopAir(ResourceLocation id, CallbackInfoReturnable<Optional<StructureTemplate>> cir) {
        if (!CoOConfig.vanillaTrimTemplateTopAir) {
            return;
        }
        Optional<StructureTemplate> loaded = cir.getReturnValue();
        if (loaded == null || loaded.isEmpty()) {
            return;
        }
        StructureTemplateAccessor template = (StructureTemplateAccessor) loaded.get();
        int ceiling = -1;
        for (StructureTemplate.StructureEntityInfo entity : template.coo$entityInfoList()) {
            ceiling = Math.max(ceiling, entity.blockPos.getY());
        }
        for (StructureTemplate.Palette palette : template.coo$palettes()) {
            for (StructureTemplate.StructureBlockInfo block : palette.blocks()) {
                if (!block.state().isAir()) {
                    ceiling = Math.max(ceiling, block.pos().getY());
                }
            }
        }
        if (ceiling < 0) {
            return;
        }
        int top = ceiling;
        for (StructureTemplate.Palette palette : template.coo$palettes()) {
            List<StructureTemplate.StructureBlockInfo> blocks = palette.blocks();
            if (blocks.removeIf(block -> block.pos().getY() > top && block.state().isAir())
                    && blocks instanceof ArrayList<?> list) {
                list.trimToSize();
            }
        }
    }
}
