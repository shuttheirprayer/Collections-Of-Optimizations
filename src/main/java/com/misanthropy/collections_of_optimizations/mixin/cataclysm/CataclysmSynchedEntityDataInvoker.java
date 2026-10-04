package com.misanthropy.collections_of_optimizations.mixin.cataclysm;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SynchedEntityData.class)
public interface CataclysmSynchedEntityDataInvoker {

    @Invoker("getItem")
    <T> SynchedEntityData.DataItem<T> coo$getItem(EntityDataAccessor<T> key);
}
