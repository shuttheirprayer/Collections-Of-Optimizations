package com.misanthropy.collections_of_optimizations.mixin.sanguinenetworks;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.leo.sanguine_networks.block.entity.VSBlockEntity", remap = false)
public abstract class MixinSnnSacrificerTick {

    @Shadow(remap = false) private int catalystUses;
    @Shadow(remap = false) private int maxCatalystUses;
    @Shadow(remap = false) private int progress;
    @Shadow(remap = false) private int toProduce;
    @Shadow(remap = false) private float catalystMult;
    @Shadow(remap = false) private boolean missingModel;

    @Unique private boolean coo$inTick;
    @Unique private boolean coo$forceSync;
    @Unique private boolean coo$sent;
    @Unique private int coo$sentCatalystUses;
    @Unique private int coo$sentMaxCatalystUses;
    @Unique private int coo$sentToProduce;
    @Unique private int coo$sentCatalystMult;
    @Unique private boolean coo$sentMissingModel;
    @Unique private boolean coo$sentIdle;

    @Unique private ItemStack coo$statsStack;
    @Unique private int coo$statsCount;
    @Unique private long coo$statsTime = Long.MIN_VALUE;
    @Unique private Level coo$statsLevel;
    @Unique private Object coo$statsResult;

    @Inject(method = "tick", at = @At("HEAD"), remap = false, require = 0)
    private void coo$enterTick(CallbackInfo ci) {
        this.coo$inTick = true;
        this.coo$forceSync = false;
    }

    @Inject(method = "tick", at = @At("RETURN"), remap = false, require = 0)
    private void coo$leaveTick(CallbackInfo ci) {
        this.coo$inTick = false;
        this.coo$forceSync = false;
    }

    @Inject(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lwayoftime/bloodmagic/common/tile/TileAltar;fillMainTank(I)I"),
            remap = false,
            require = 0
    )
    private void coo$markCycleDone(CallbackInfo ci) {
        this.coo$forceSync = true;
        this.coo$statsResult = null;
    }

    @Inject(method = "sync", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void coo$throttleSync(CallbackInfo ci) {
        BlockEntity self = (BlockEntity) (Object) this;
        Level level = self.getLevel();
        if (level == null) {
            return;
        }
        if (level.isClientSide) {
            if (CoOConfig.sanguinenetworksSkipClientRerender) {
                self.setChanged();
                ci.cancel();
            }
            return;
        }
        if (!CoOConfig.sanguinenetworksThrottleSync || !this.coo$inTick) {
            this.coo$remember();
            return;
        }
        if (this.coo$forceSync) {
            this.coo$forceSync = false;
            this.coo$remember();
            return;
        }
        if (this.coo$sent
                && this.coo$sentCatalystUses == this.catalystUses
                && this.coo$sentMaxCatalystUses == this.maxCatalystUses
                && this.coo$sentToProduce == this.toProduce
                && this.coo$sentCatalystMult == Float.floatToRawIntBits(this.catalystMult)
                && this.coo$sentMissingModel == this.missingModel
                && this.coo$sentIdle == (this.progress == 0)) {
            self.setChanged();
            ci.cancel();
            return;
        }
        this.coo$remember();
    }

    @Unique
    private void coo$remember() {
        this.coo$sent = true;
        this.coo$sentCatalystUses = this.catalystUses;
        this.coo$sentMaxCatalystUses = this.maxCatalystUses;
        this.coo$sentToProduce = this.toProduce;
        this.coo$sentCatalystMult = Float.floatToRawIntBits(this.catalystMult);
        this.coo$sentMissingModel = this.missingModel;
        this.coo$sentIdle = this.progress == 0;
    }

    @Inject(
            method = "getModelFromStack(Lnet/minecraft/world/item/ItemStack;)Lcom/leo/sanguine_networks/util/Pair;",
            at = @At("HEAD"),
            cancellable = true,
            remap = false,
            require = 0
    )
    private void coo$readTickStats(ItemStack stack, CallbackInfoReturnable<Object> cir) {
        if (!CoOConfig.sanguinenetworksCacheTickStats) {
            return;
        }
        Level level = ((BlockEntity) (Object) this).getLevel();
        if (level == null || this.coo$statsResult == null) {
            return;
        }
        if (stack == this.coo$statsStack
                && level == this.coo$statsLevel
                && stack.getCount() == this.coo$statsCount
                && level.getGameTime() == this.coo$statsTime) {
            cir.setReturnValue(this.coo$statsResult);
        }
    }

    @Inject(
            method = "getModelFromStack(Lnet/minecraft/world/item/ItemStack;)Lcom/leo/sanguine_networks/util/Pair;",
            at = @At("RETURN"),
            remap = false,
            require = 0
    )
    private void coo$storeTickStats(ItemStack stack, CallbackInfoReturnable<Object> cir) {
        if (!CoOConfig.sanguinenetworksCacheTickStats) {
            return;
        }
        Level level = ((BlockEntity) (Object) this).getLevel();
        if (level == null || stack == null) {
            this.coo$statsResult = null;
            return;
        }
        this.coo$statsStack = stack;
        this.coo$statsCount = stack.getCount();
        this.coo$statsLevel = level;
        this.coo$statsTime = level.getGameTime();
        this.coo$statsResult = cir.getReturnValue();
    }
}
