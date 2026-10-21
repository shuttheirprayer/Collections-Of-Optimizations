package com.misanthropy.collections_of_optimizations.mixin.lootr;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.server.ServerLifecycleHooks;
import noobanidus.mods.lootr.block.entities.TileTicker;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

@Mixin(value = TileTicker.class, remap = false)
public abstract class MixinTileTicker {

    @Shadow
    @Final
    private static Set<TileTicker.Entry> tileEntries;

    @Shadow
    @Final
    private static Set<TileTicker.Entry> pendingEntries;

    @Inject(
            method = "serverTick(Lnet/minecraftforge/event/TickEvent$ServerTickEvent;)V",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void coo$skipIdlePass(TickEvent.ServerTickEvent event, CallbackInfo ci) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (CoOConfig.lootrTileTickerInterval > 1 && ServerLifecycleHooks.getCurrentServer().getTickCount() % CoOConfig.lootrTileTickerInterval != 0) {
            ci.cancel();
            return;
        }
        if (!CoOConfig.lootrSkipIdleTileTicker) {
            return;
        }
        if (tileEntries.isEmpty() && pendingEntries.isEmpty()) {
            ci.cancel();
        }
    }

    @WrapOperation(
            method = "serverTick(Lnet/minecraftforge/event/TickEvent$ServerTickEvent;)V",
            at = @At(
                    value = "NEW",
                    target = "(Ljava/util/Collection;)Lit/unimi/dsi/fastutil/objects/ObjectOpenHashSet;",
                    ordinal = 1
            ),
            require = 0
    )
    private static ObjectOpenHashSet coo$boundWorkingCopy(Collection source, Operation<ObjectOpenHashSet> original) {
        int budget = CoOConfig.lootrTileTickerBudget;
        if (budget <= 0 || source.size() <= budget) {
            return original.call(source);
        }

        ObjectOpenHashSet<Object> bounded = new ObjectOpenHashSet<>(budget);
        Iterator<?> iterator = source.iterator();
        for (int i = 0; i < budget && iterator.hasNext(); i++) {
            bounded.add(iterator.next());
        }
        return bounded;
    }
}
