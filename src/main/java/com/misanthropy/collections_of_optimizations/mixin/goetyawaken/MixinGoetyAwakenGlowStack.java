package com.misanthropy.collections_of_optimizations.mixin.goetyawaken;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ClientTickStamp;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;

import java.lang.ref.WeakReference;

@Pseudo
@Mixin(targets = "com.k1sak1.goetyawaken.common.items.magic.TransfigurationFocus", remap = false)
public abstract class MixinGoetyAwakenGlowStack {

    @Unique
    private static WeakReference<LivingEntity> coo$holder = new WeakReference<>(null);
    @Unique
    private static ItemStack coo$stack = ItemStack.EMPTY;
    @Unique
    private static int coo$stamp = Integer.MIN_VALUE;

    @WrapMethod(method = "getActiveStack", require = 0)
    private static ItemStack coo$lookUpOncePerTick(LivingEntity entity, Operation<ItemStack> original) {
        if (!CoOConfig.goetyawakenScanOncePerTick || !(entity instanceof LocalPlayer)) {
            return original.call(entity);
        }
        int tick = ClientTickStamp.current();
        if (tick != coo$stamp || coo$holder.get() != entity) {
            coo$stack = original.call(entity);
            coo$holder = new WeakReference<>(entity);
            coo$stamp = tick;
        }
        return coo$stack;
    }
}
