package com.misanthropy.collections_of_optimizations.mixin.cataclysm;

import com.github.L_Ender.cataclysm.capabilities.TidalTentacleCapability;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = TidalTentacleCapability.TentacleCapabilityImp.class, remap = false)
public abstract class MixinCataclysmTentacleCapLoad {

    @Shadow
    public abstract void setHasTentacle(boolean hasTentacle);

    @Shadow
    public abstract void setLastTentacleID(int id);

    @WrapMethod(method = "deserializeNBT(Lnet/minecraft/nbt/CompoundTag;)V", require = 0)
    private void coo$noThrowWithoutUuid(CompoundTag nbt, Operation<Void> original) {
        if (!CoOConfig.cataclysmQuietTentacleLoad || nbt.contains("getLastTentacleUUID")) {
            original.call(nbt);
            return;
        }
        this.setHasTentacle(nbt.getBoolean("hasTentacle"));
        this.setLastTentacleID(nbt.getInt("getLastTentacleID"));
    }
}
