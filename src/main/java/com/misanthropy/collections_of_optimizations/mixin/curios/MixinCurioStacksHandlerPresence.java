package com.misanthropy.collections_of_optimizations.mixin.curios;

import com.misanthropy.collections_of_optimizations.core.CurioOwnedStacks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;
import top.theillusivec4.curios.common.inventory.CurioStacksHandler;

@Mixin(value = CurioStacksHandler.class, remap = false)
public abstract class MixinCurioStacksHandlerPresence {

    @Shadow
    @Final
    private ICuriosItemHandler itemHandler;

    @Shadow
    private IDynamicStackHandler stackHandler;

    @Shadow
    private IDynamicStackHandler cosmeticStackHandler;

    @Inject(method = "<init>(Ltop/theillusivec4/curios/api/type/capability/ICuriosItemHandler;Ljava/lang/String;IZZZLtop/theillusivec4/curios/api/type/capability/ICurio$DropRule;)V", at = @At("RETURN"))
    private void coo$tagOwner(CallbackInfo ci) {
        if (this.stackHandler instanceof CurioOwnedStacks owned) {
            owned.coo$setCurioOwner(this.itemHandler);
        }
        if (this.cosmeticStackHandler instanceof CurioOwnedStacks owned) {
            owned.coo$setCurioOwner(this.itemHandler);
        }
    }
}
