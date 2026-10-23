package com.misanthropy.collections_of_optimizations.mixin.touchnmotion;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.ProbeCheck;
import com.misanthropy.collections_of_optimizations.core.TouchNMotionFloor;
import java.util.List;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import strm.touchnmotion.interaction.SubLevels;

@Mixin(targets = "strm.touchnmotion.footgrounding.compat.TerrainBalance", remap = false)
public abstract class MixinTerrainBalanceFloor {

    @Unique
    private static boolean coo$runningStock;

    @Inject(method = "floor(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/world/phys/Vec3;Ljava/util/List;)Lstrm/touchnmotion/footgrounding/compat/TerrainBalance$Floor;",
            at = @At("HEAD"), cancellable = true)
    private static void coo$worldFloorWithoutClipContexts(AbstractClientPlayer player, Vec3 foot, List<SubLevels.Space> spaces, CallbackInfoReturnable<Object> cir) throws Throwable {
        if (!CoOConfig.touchnmotionFastFloor || coo$runningStock || spaces.size() != 1 || spaces.get(0) != SubLevels.WORLD) return;
        Object fast = TouchNMotionFloor.floor(player, foot);
        if (ProbeCheck.ON) {
            coo$runningStock = true;
            try {
                ProbeCheck.compareObjects("TerrainBalance.floor", fast, coo$stock(player, foot, spaces));
            } finally {
                coo$runningStock = false;
            }
        }
        cir.setReturnValue(fast);
    }

    @Unique
    private static Object coo$stock(AbstractClientPlayer player, Vec3 foot, List<SubLevels.Space> spaces) throws Throwable {
        return TouchNMotionFloor.stock(player, foot, spaces);
    }
}
