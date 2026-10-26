package com.misanthropy.collections_of_optimizations.mixin.xaerominimap;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.FrameDeferred;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import xaero.common.HudMod;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.hud.minimap.radar.RadarSession;
import xaero.hud.minimap.radar.state.RadarList;

@Mixin(value = RadarSession.class, remap = false)
public abstract class MixinRadarSessionDeferredUpdate {

    @Unique
    private boolean coo$cleared;

    @WrapMethod(method = "update")
    private void coo$deferRadarUpdate(ClientLevel world, Entity renderEntity, Player player, Operation<Void> original) {
        if (CoOConfig.xaeroMinimapSkipHiddenRadar && Minecraft.getInstance().screen == null
                && !(Boolean) HudMod.INSTANCE.getHudConfigs().getClientConfigManager().getEffective(MinimapProfiledConfigOptions.DISPLAY_MINIMAP)) {
            if (!this.coo$cleared) {
                ((RadarSession) (Object) this).getState().getRadarLists().forEach(RadarList::clearEntities);
                this.coo$cleared = true;
            }
            return;
        }
        this.coo$cleared = false;
        if (!CoOConfig.xaeroMinimapDeferRadarUpdate || !FrameDeferred.ticking()) {
            original.call(world, renderEntity, player);
            return;
        }
        FrameDeferred.defer(Minecraft.getInstance().level, () -> original.call(world, renderEntity, player));
    }
}
