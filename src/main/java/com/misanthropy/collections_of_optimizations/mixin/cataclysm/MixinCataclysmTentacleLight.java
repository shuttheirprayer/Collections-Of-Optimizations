package com.misanthropy.collections_of_optimizations.mixin.cataclysm;

import com.github.L_Ender.cataclysm.client.render.entity.The_Leviathan_Renderer;
import com.github.L_Ender.cataclysm.client.render.entity.Tidal_Tentacle_Renderer;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {The_Leviathan_Renderer.class, Tidal_Tentacle_Renderer.class}, remap = false)
public abstract class MixinCataclysmTentacleLight {

    @Unique
    private static final int coo$SIZE = 16;

    @Unique
    private static final int[] coo$xs = new int[coo$SIZE], coo$ys = new int[coo$SIZE], coo$zs = new int[coo$SIZE], coo$lights = new int[coo$SIZE];

    @Unique
    private static int coo$count;

    @Unique
    private static int coo$next;

    @Unique
    private static Level coo$level;

    @Shadow(remap = false)
    private int getLightColor(Entity head, Vec3 pos) {
        throw new AssertionError();
    }

    @Inject(method = "render", at = @At("HEAD"), require = 0)
    private void coo$clearLightMemo(CallbackInfo ci) {
        coo$count = 0;
        coo$next = 0;
        coo$level = null;
    }

    @Redirect(
            method = "render",
            at = @At(value = "INVOKE", target = "Lcom/github/L_Ender/cataclysm/client/render/entity/The_Leviathan_Renderer;getLightColor(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;)I"),
            require = 0
    )
    private int coo$leviathanLight(The_Leviathan_Renderer self, Entity head, Vec3 pos) {
        return this.coo$light(head, pos);
    }

    @Redirect(
            method = "render",
            at = @At(value = "INVOKE", target = "Lcom/github/L_Ender/cataclysm/client/render/entity/Tidal_Tentacle_Renderer;getLightColor(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;)I"),
            require = 0
    )
    private int coo$tentacleLight(Tidal_Tentacle_Renderer self, Entity head, Vec3 pos) {
        return this.coo$light(head, pos);
    }

    @Unique
    private int coo$light(Entity head, Vec3 pos) {
        if (!CoOConfig.cataclysmCacheTentacleLight) {
            return this.getLightColor(head, pos);
        }
        int x = Mth.floor(pos.x);
        int y = Mth.floor(pos.y);
        int z = Mth.floor(pos.z);
        Level level = head.level();
        if (level != coo$level) {
            coo$level = level;
            coo$count = 0;
            coo$next = 0;
        }
        int[] xs = coo$xs, ys = coo$ys, zs = coo$zs;
        for (int i = 0, n = coo$count; i < n; i++) {
            if (xs[i] == x && ys[i] == y && zs[i] == z) {
                return coo$lights[i];
            }
        }
        int light = this.getLightColor(head, pos);
        int slot = coo$next;
        xs[slot] = x;
        ys[slot] = y;
        zs[slot] = z;
        coo$lights[slot] = light;
        coo$next = (slot + 1) % coo$SIZE;
        if (coo$count < coo$SIZE) {
            coo$count++;
        }
        return light;
    }
}
