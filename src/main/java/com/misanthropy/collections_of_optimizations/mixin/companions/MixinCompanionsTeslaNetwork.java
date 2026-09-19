package com.misanthropy.collections_of_optimizations.mixin.companions;

import com.misanthropy.collections_of_optimizations.core.CompanionsTeslaState;
import dev.xylonity.companions.common.blockentity.AbstractTeslaBlockEntity;
import dev.xylonity.companions.common.tesla.TeslaNetwork;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.Map;

@Mixin(value = TeslaNetwork.class, remap = false)
public abstract class MixinCompanionsTeslaNetwork implements CompanionsTeslaState.Network {

    @Shadow
    @Final
    private Map<BlockPos, AbstractTeslaBlockEntity> blockEntities;

    @Shadow
    public abstract void recalculateDistances();

    @Inject(method = "<init>", at = @At("RETURN"), require = 0)
    private void coo$trackNetwork(CallbackInfo ci) {
        CompanionsTeslaState.track(this);
    }

    @Inject(method = "recalculateDistances", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$deferRebuild(CallbackInfo ci) {
        if (CompanionsTeslaState.defer(this)) {
            ci.cancel();
        }
    }

    @Inject(method = "clearAll", at = @At("TAIL"), require = 0)
    private static void coo$forgetNetworks(CallbackInfo ci) {
        CompanionsTeslaState.clear();
    }

    @Override
    public void coo$rebuildNow() {
        this.recalculateDistances();
    }

    @Override
    public void coo$dropRemovedBlockEntities() {
        Iterator<Map.Entry<BlockPos, AbstractTeslaBlockEntity>> iterator = this.blockEntities.entrySet().iterator();
        while (iterator.hasNext()) {
            AbstractTeslaBlockEntity blockEntity = iterator.next().getValue();
            if (blockEntity == null || blockEntity.isRemoved() || blockEntity.getLevel() == null) {
                iterator.remove();
            }
        }
    }
}
