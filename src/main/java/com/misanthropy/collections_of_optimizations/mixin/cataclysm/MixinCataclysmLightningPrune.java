package com.misanthropy.collections_of_optimizations.mixin.cataclysm;

import com.github.L_Ender.cataclysm.client.render.entity.Boltstrike_Renderer;
import com.github.L_Ender.cataclysm.client.render.entity.Death_Laser_beam_Renderer;
import com.github.L_Ender.cataclysm.client.render.etc.LightningRender;
import com.github.L_Ender.cataclysm.client.render.layer.Maledictus_Cicle_Layer;
import com.github.L_Ender.cataclysm.client.render.layer.Scylla_Anchor_Layer;
import com.github.L_Ender.cataclysm.client.render.layer.Scylla_Eye_Spark_Layer;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

@Mixin(value = {Boltstrike_Renderer.class, Death_Laser_beam_Renderer.class, Maledictus_Cicle_Layer.class, Scylla_Anchor_Layer.class, Scylla_Eye_Spark_Layer.class}, remap = false)
public abstract class MixinCataclysmLightningPrune {

    @Shadow(remap = false)
    private Map<UUID, LightningRender> lightningRenderMap;

    @Inject(method = "render", at = @At("HEAD"), require = 0)
    private void coo$pruneLightning(CallbackInfo ci) {
        Map<UUID, LightningRender> map = this.lightningRenderMap;
        if (!CoOConfig.cataclysmPruneLightningRenders || map == null || map.isEmpty()) {
            return;
        }
        Level current = Minecraft.getInstance().level;
        Iterator<LightningRender> renders = map.values().iterator();
        while (renders.hasNext()) {
            if (coo$stale(((CataclysmLightningRenderAccessor) renders.next()).coo$boltOwners(), current)) {
                renders.remove();
            }
        }
    }

    @Unique
    private static boolean coo$stale(Map<Object, ?> owners, Level current) {
        if (owners.isEmpty()) {
            return false;
        }
        for (Object owner : owners.keySet()) {
            if (!(owner instanceof Entity entity) || (!entity.isRemoved() && entity.level() == current)) {
                return false;
            }
        }
        return true;
    }
}
