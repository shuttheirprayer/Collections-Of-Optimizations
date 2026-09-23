package com.misanthropy.collections_of_optimizations.mixin.goetycataclysm;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraftforge.event.level.LevelEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.Polarice3.goety_cataclysm.common.events.GCEvents", remap = false)
public abstract class MixinGcEvents {

    @Inject(method = "worldUnload", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$skipUnloadSweep(LevelEvent.Unload event, CallbackInfo ci) {
        if (CoOConfig.goetycataclysmSkipGolemUnloadSweep) {
            ci.cancel();
        }
    }

    @WrapOperation(
            method = "LootEvents",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/Component;getString()Ljava/lang/String;"
            ),
            require = 0
    )
    private static String coo$descriptionKey(Component component, Operation<String> original) {
        if (CoOConfig.goetycataclysmFixWantLootingCheck
                && component.getContents() instanceof TranslatableContents translatable) {
            return translatable.getKey();
        }
        return original.call(component);
    }
}
