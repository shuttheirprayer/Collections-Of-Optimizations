package com.misanthropy.collections_of_optimizations.mixin.cataclysm;

import com.misanthropy.collections_of_optimizations.core.CataclysmCapHolder;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntityCataclysmCaps implements CataclysmCapHolder {

    @Unique
    private Object[] coo$cataclysmCaps;

    @Override
    public Object coo$getCataclysmCap(int index) {
        Object[] slots = this.coo$cataclysmCaps;
        return slots == null ? null : slots[index];
    }

    @Override
    public void coo$setCataclysmCap(int index, Object value) {
        Object[] slots = this.coo$cataclysmCaps;
        if (slots == null) {
            slots = new Object[CataclysmCapHolder.COO_CAP_COUNT];
            this.coo$cataclysmCaps = slots;
        }
        slots[index] = value;
    }
}
