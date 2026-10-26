package com.misanthropy.collections_of_optimizations.mixin.create;

import com.misanthropy.collections_of_optimizations.core.CreateBehaviourTickFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(targets = "com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour", remap = false)
public abstract class MixinBlockEntityBehaviourTickFlag implements CreateBehaviourTickFlag {

    @Unique
    private byte coo$tickFlag;

    @Override
    public byte coo$tickFlag() {
        return this.coo$tickFlag;
    }

    @Override
    public void coo$setTickFlag(byte flag) {
        this.coo$tickFlag = flag;
    }
}
