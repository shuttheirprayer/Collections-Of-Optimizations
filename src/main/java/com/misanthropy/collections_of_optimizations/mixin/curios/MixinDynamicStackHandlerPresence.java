package com.misanthropy.collections_of_optimizations.mixin.curios;

import com.misanthropy.collections_of_optimizations.core.CurioOwnedStacks;
import com.misanthropy.collections_of_optimizations.core.CurioPresenceCache;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.common.inventory.DynamicStackHandler;

@Mixin(value = DynamicStackHandler.class, remap = false)
public abstract class MixinDynamicStackHandlerPresence extends ItemStackHandler implements CurioOwnedStacks {

    @Unique
    private ICuriosItemHandler coo$curioOwner;

    @Override
    public void coo$setCurioOwner(ICuriosItemHandler owner) {
        this.coo$curioOwner = owner;
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        this.coo$invalidateOwner();
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        this.coo$invalidateOwner();
    }

    @Unique
    private void coo$invalidateOwner() {
        if (this.coo$curioOwner != null) {
            CurioPresenceCache.invalidate(this.coo$curioOwner.getWearer());
        }
    }
}
