package com.misanthropy.collections_of_optimizations.mixin.cataclysm;

import com.github.L_Ender.cataclysm.init.ModTag;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

@Pseudo
@Mixin(targets = "com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.Old_Netherite_Monstrosity_Entity", remap = false)
public abstract class MixinCataclysmOldMonstrosityBlockBreaking {

    @Shadow
    private int blockBreakCounter;

    @Shadow
    private boolean shouldDropItem(BlockEntity tileEntity) {
        throw new AssertionError();
    }

    @WrapMethod(method = "BlockBreaking", require = 0)
    private void coo$leanBlockBreaking(Operation<Void> original) {
        if (!CoOConfig.cataclysmLeanMonstrosityBlockBreaking) {
            original.call();
            return;
        }
        if (this.blockBreakCounter > 0) {
            this.blockBreakCounter--;
            return;
        }
        Mob self = (Mob) (Object) this;
        Level level = self.level();
        if (!self.isNoAi() && !level.isClientSide && this.blockBreakCounter == 0
                && ForgeEventFactory.getMobGriefingEvent(level, self)) {
            BlockState air = Blocks.AIR.defaultBlockState();
            BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
            for (int a = (int) Math.round(self.getBoundingBox().minX); a <= (int) Math.round(self.getBoundingBox().maxX); a++) {
                for (int b = (int) Math.round(self.getBoundingBox().minY); b <= (int) Math.round(self.getBoundingBox().maxY) + 1 && b <= 127; b++) {
                    for (int c = (int) Math.round(self.getBoundingBox().minZ); c <= (int) Math.round(self.getBoundingBox().maxZ); c++) {
                        BlockState block = level.getBlockState(cursor.set(a, b, c));
                        if (block != air && block.is(ModTag.NETHERITE_MONSTROSITY_BREAK)) {
                            BlockPos pos = cursor.immutable();
                            BlockEntity tileEntity = level.getBlockEntity(pos);
                            if (level.destroyBlock(pos, this.shouldDropItem(tileEntity))) {
                                this.blockBreakCounter = 10;
                            }
                        } else if (block.hasBlockEntity()) {
                            level.getBlockEntity(cursor.immutable());
                        }
                    }
                }
            }
        }
    }
}
