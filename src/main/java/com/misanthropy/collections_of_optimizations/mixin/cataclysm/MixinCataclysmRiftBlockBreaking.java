package com.misanthropy.collections_of_optimizations.mixin.cataclysm;

import com.github.L_Ender.cataclysm.entity.effect.Cm_Falling_Block_Entity;
import com.github.L_Ender.cataclysm.init.ModTag;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(targets = "com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.The_Leviathan.Dimensional_Rift_Entity", remap = false)
public abstract class MixinCataclysmRiftBlockBreaking {

    @WrapMethod(method = "berserkBlockBreaking", require = 0)
    private void coo$leanBerserkBlockBreaking(int x, int y, int z, Operation<Void> original) {
        Entity self = (Entity) (Object) this;
        Level level = self.level();
        if (!CoOConfig.cataclysmLeanRiftBlockBreaking || level.isClientSide || level.isDebug()) {
            original.call(x, y, z);
            return;
        }
        int mthX = Mth.floor(self.getX());
        int mthY = Mth.floor(self.getY());
        int mthZ = Mth.floor(self.getZ());
        if (!ForgeEventFactory.getMobGriefingEvent(level, self)) {
            return;
        }
        BlockState air = Blocks.AIR.defaultBlockState();
        BlockState water = Blocks.WATER.defaultBlockState();
        RandomSource random = ((MixinCataclysmEntityAccessor) self).coo$random();
        int minY = level.getMinBuildHeight();
        int maxY = level.getMaxBuildHeight();
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int k2 = -x; k2 <= x; k2++) {
            for (int l2 = -z; l2 <= z; l2++) {
                int i3 = mthX + k2;
                int l = mthZ + l2;
                LevelChunk chunk = null;
                BlockState carried = null;
                for (int j = -y; j <= y; j++) {
                    int k = mthY + j;
                    BlockState block;
                    if (carried != null) {
                        block = carried;
                        carried = null;
                    } else {
                        if (k >= minY && k < maxY) {
                            if (chunk == null) {
                                chunk = level.getChunk(i3 >> 4, l >> 4);
                            }
                            if (chunk.getSection(chunk.getSectionIndex(k)).hasOnlyAir()) {
                                continue;
                            }
                        }
                        block = level.getBlockState(cursor.set(i3, k, l));
                    }
                    if (block == air) {
                        continue;
                    }
                    BlockState blockon = level.getBlockState(cursor.set(i3, k + 1, l));
                    carried = blockon;
                    BlockEntity tileEntity = null;
                    if (block.hasBlockEntity()) {
                        tileEntity = level.getBlockEntity(new BlockPos(i3, k, l));
                        carried = null;
                    }
                    if ((blockon != air && blockon != water) || block.is(ModTag.LEVIATHAN_IMMUNE)) {
                        continue;
                    }
                    if (!block.hasBlockEntity()) {
                        tileEntity = level.getBlockEntity(cursor.set(i3, k, l));
                    }
                    if (tileEntity != null || random.nextInt(2000) != 0) {
                        continue;
                    }
                    carried = null;
                    BlockPos blockpos = new BlockPos(i3, k, l);
                    level.removeBlock(blockpos, true);
                    Cm_Falling_Block_Entity fallingBlockEntity = new Cm_Falling_Block_Entity(level, i3 + 0.5, k + 0.5, l + 0.5, block, 5);
                    level.setBlock(blockpos, block.getFluidState().createLegacyBlock(), 3);
                    level.addFreshEntity(fallingBlockEntity);
                }
            }
        }
    }
}
