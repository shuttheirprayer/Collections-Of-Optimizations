package com.misanthropy.collections_of_optimizations.mixin.copycats;

import com.copycatsplus.copycats.foundation.copycat.CCCopycatBlock;
import com.copycatsplus.copycats.foundation.copycat.MigrationManager;
import com.copycatsplus.copycats.foundation.copycat.multistate.MultiStateCopycatBlock;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(StructureTemplate.class)
public abstract class MixinStructureTemplateMigration {

    @ModifyExpressionValue(method = "loadPalette", at = @At(value = "NEW", target = "net/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate$StructureBlockInfo"))
    private StructureTemplate.StructureBlockInfo coo$migrateCopycats(StructureTemplate.StructureBlockInfo info) {
        Block block = info.state().getBlock();
        if (block instanceof MultiStateCopycatBlock || block instanceof CCCopycatBlock) {
            return MigrationManager.migrateStructure(info);
        }
        return info;
    }
}
