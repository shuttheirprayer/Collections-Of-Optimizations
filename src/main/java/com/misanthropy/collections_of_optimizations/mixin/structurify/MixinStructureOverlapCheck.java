package com.misanthropy.collections_of_optimizations.mixin.structurify;

import com.faboslav.structurify.common.world.level.structure.checks.StructureOverlapCheck;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = StructureOverlapCheck.class, remap = false)
public abstract class MixinStructureOverlapCheck {

    // newer versions keep their own logic
    @Inject(
            method = "getStructurePiecesSections(Lnet/minecraft/world/level/levelgen/structure/StructureStart;)[J",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$unboxedSectionKeys(StructureStart start, CallbackInfoReturnable<long[]> cir) {
        if (!CoOConfig.structurifyLeanOverlapSections) {
            return;
        }
        LongOpenHashSet sections = new LongOpenHashSet();
        for (StructurePiece piece : start.getPieces()) {
            BoundingBox box = piece.getBoundingBox();
            int minSectionX = SectionPos.blockToSectionCoord(box.minX());
            int maxSectionX = SectionPos.blockToSectionCoord(box.maxX());
            int minSectionZ = SectionPos.blockToSectionCoord(box.minZ());
            int maxSectionZ = SectionPos.blockToSectionCoord(box.maxZ());
            int minSectionY = SectionPos.blockToSectionCoord(box.minY());
            int maxSectionY = SectionPos.blockToSectionCoord(box.maxY());

            for (int sectionZ = minSectionZ; sectionZ <= maxSectionZ; sectionZ++) {
                for (int sectionX = minSectionX; sectionX <= maxSectionX; sectionX++) {
                    for (int sectionY = minSectionY; sectionY <= maxSectionY; sectionY++) {
                        sections.add(SectionPos.asLong(sectionX, sectionY, sectionZ));
                    }
                }
            }
        }
        cir.setReturnValue(sections.toLongArray());
    }
}
