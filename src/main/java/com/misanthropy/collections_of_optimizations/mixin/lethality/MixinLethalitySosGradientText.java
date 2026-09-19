package com.misanthropy.collections_of_optimizations.mixin.lethality;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = {
        "net.daphne.lethality.item.custom.curios.BackupSOSItem",
        "net.daphne.lethality.item.custom.curios.UltraBackupSOSItem"
}, remap = false)
public abstract class MixinLethalitySosGradientText {

    @ModifyExpressionValue(
            method = {
                    "addColorGradientText(Lnet/minecraft/network/chat/Component;FF[[I)Lnet/minecraft/network/chat/MutableComponent;",
                    "addColorGradientTextWithFont(Lnet/minecraft/network/chat/Component;Lnet/minecraft/resources/ResourceLocation;FF[[I)Lnet/minecraft/network/chat/MutableComponent;"
            },
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/fml/DistExecutor;unsafeCallWhenOn(Lnet/minecraftforge/api/distmarker/Dist;Ljava/util/function/Supplier;)Ljava/lang/Object;"
            ),
            require = 0
    )
    private static Object coo$serverSafeGradientTick(Object original) {
        if (original != null || !CoOConfig.lethalityFixServerSideSosName) {
            return original;
        }
        return Integer.valueOf((int) (System.currentTimeMillis() / 50L));
    }
}
