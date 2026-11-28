package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.IdleEntities;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = ItemEntity.class, priority = 3000)
public abstract class MixinItemEntityIdle implements IdleEntities.State {

    @Shadow
    private int age;

    @Shadow
    private int pickupDelay;

    @Shadow(remap = false)
    public int lifespan;

    @Shadow
    private boolean isMergable() {
        throw new AssertionError();
    }

    @Shadow
    private void mergeWithNeighbours() {
        throw new AssertionError();
    }

    @Shadow
    private void tryToMerge(ItemEntity other) {
        throw new AssertionError();
    }

    @Override
    public boolean coo$mergeable() {
        return this.isMergable();
    }

    @Override
    public boolean coo$skipTick() {
        ItemEntity self = (ItemEntity) (Object) this;
        if (!IdleEntities.asleep(self)) {
            if ((self.tickCount & 7) != 0 || !CoOConfig.vanillaSleepIdleItems || self.getClass() != ItemEntity.class) {
                return false;
            }
            ItemStack stack = self.getItem();
            if (stack.isEmpty() || IdleEntities.itemHasUpdateHook(stack.getItem())) {
                return false;
            }
            IdleEntities.trySleep(self, true, true);
            if (!IdleEntities.asleep(self)) {
                return false;
            }
            if (CoOConfig.vanillaMergeSettledItems && this.isMergable()) {
                for (ItemEntity other : self.level().getEntitiesOfClass(ItemEntity.class, self.getBoundingBox().inflate(1.0D, 0.25D, 1.0D),
                        other -> other != self && IdleEntities.asleep(other) && ((IdleEntities.State) other).coo$mergeable())) {
                    this.tryToMerge(other);
                    if (self.isRemoved()) {
                        return true;
                    }
                }
            }
        }
        if (CoOConfig.vanillaSleepIdleItems && this.coo$keepSleeping(self)) {
            return true;
        }
        this.coo$wake();
        return false;
    }

    private boolean coo$keepSleeping(ItemEntity self) {
        if (!IdleEntities.stillIdle(self)) {
            return false;
        }
        if (((self.tickCount + self.getId()) & 3) == 0 && (!IdleEntities.settled(self, true, true) || self.getItem().isEmpty())) {
            return false;
        }
        int current = this.age;
        if (current != -32768 && current + 1 >= this.lifespan) {
            return false;
        }
        if (current != -32768) {
            this.age = current + 1;
        }
        if (this.pickupDelay > 0 && this.pickupDelay != 32767) {
            this.pickupDelay--;
        }
        if (self.tickCount % 40 == 0 && this.isMergable()) {
            this.mergeWithNeighbours();
        }
        return true;
    }
}
