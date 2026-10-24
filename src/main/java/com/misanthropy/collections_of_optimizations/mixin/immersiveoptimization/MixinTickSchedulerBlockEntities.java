package com.misanthropy.collections_of_optimizations.mixin.immersiveoptimization;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ImmersiveBlockEntitySchedule;
import net.conczin.immersive_optimization.TickScheduler;
import net.conczin.immersive_optimization.config.Config;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = TickScheduler.class, remap = false)
public abstract class MixinTickSchedulerBlockEntities {

    @Shadow
    public abstract TickScheduler.LevelData getLevelData(Level level);

    @Shadow
    private int getBlockEntityPriority(Level level, long pos) {
        throw new AssertionError();
    }

    @Overwrite
    public boolean shouldTick(Level level, long pos) {
        if (!Config.getInstance().enableBlockEntities) {
            return true;
        }
        if (!CoOConfig.immersiveoptimizationFastBlockEntitySchedule) {
            TickScheduler.LevelData data = this.getLevelData(level);
            if (data == null || data.forcedChunks.contains(pos)) {
                return true;
            }
            int priority = data.blockEntityPriorities.computeIfAbsent(pos, p -> this.getBlockEntityPriority(level, p));
            return priority < 1 || (level.getGameTime() + pos) % (long) priority == 0L;
        }
        ImmersiveBlockEntitySchedule schedule = ((ImmersiveBlockEntitySchedule.Holder) level).coo$immersiveSchedule();
        long time = level.getGameTime();
        if (schedule.tick != time) {
            schedule.tick = time;
            schedule.levelData = this.getLevelData(level);
            if (time % 207L == 0L) {
                schedule.priorities.clear();
            }
        }
        TickScheduler.LevelData data = (TickScheduler.LevelData) schedule.levelData;
        if (data == null || data.forcedChunks.contains(pos)) {
            return true;
        }
        int priority = schedule.priorities.get(pos);
        if (priority == ImmersiveBlockEntitySchedule.UNKNOWN) {
            priority = this.getBlockEntityPriority(level, pos);
            schedule.priorities.put(pos, priority);
        }
        return priority < 1 || (time + pos) % (long) priority == 0L;
    }
}
