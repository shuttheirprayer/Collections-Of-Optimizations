package com.misanthropy.collections_of_optimizations.mixin.castercuriosbonus;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.api.SlotContext;

@Pseudo
@Mixin(targets = "com.rinko1231.ccb.item.necklace.MindToMatter", remap = false)
public abstract class MixinCcbMindToMatter {

    @Inject(method = "curioTick", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$serverOnlyManaToHealth(SlotContext context, ItemStack stack, CallbackInfo ci) {
        if (!CoOConfig.castercuriosbonusServerOnlyManaToHealth || context == null) {
            return;
        }
        LivingEntity entity = context.entity();
        if (entity != null && entity.level().isClientSide) {
            ci.cancel();
        }
    }

    @ModifyExpressionValue(
            method = "curioTick",
            at = @At(
                    value = "FIELD",
                    opcode = Opcodes.GETFIELD,
                    target = "Lcom/rinko1231/ccb/item/necklace/MindToMatter;tickCounterHTM:I"
            ),
            require = 0
    )
    private int coo$perWearerThrottle(int original, SlotContext context, ItemStack stack) {
        if (!CoOConfig.castercuriosbonusPerPlayerCurioThrottle || context == null) {
            return original;
        }
        LivingEntity entity = context.entity();
        if (entity == null) {
            return original;
        }
        return (entity.tickCount + entity.getId()) % 10 == 0 ? 10 : 0;
    }
}
