package com.misanthropy.collections_of_optimizations.mixin.gnetum;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import me.decce.gnetum.ASMEventHandlerHelper;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.eventbus.ASMEventHandler;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBusInvokeDispatcher;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Predicate;

@Mixin(value = ForgeGui.class, priority = 1500, remap = false)
public abstract class MixinForgeGuiOverlayModId {

    @Dynamic
    @Redirect(
            method = "gnetum$postEvent(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraftforge/eventbus/api/IEventBusInvokeDispatcher;Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/util/function/Predicate;)Z",
            at = @At(value = "INVOKE", target = "Lme/decce/gnetum/ASMEventHandlerHelper;tryGetModId(Lnet/minecraftforge/eventbus/ASMEventHandler;)Ljava/lang/String;"),
            require = 0
    )
    private String coo$modIdOnlyWhenUsed(ASMEventHandler handler, Event event, IEventBusInvokeDispatcher wrapper, PoseStack poseStack, Predicate<String> check) {
        return CoOConfig.gnetumSingleModIdLookup && event instanceof RenderGuiOverlayEvent ? null : ASMEventHandlerHelper.tryGetModId(handler);
    }
}
