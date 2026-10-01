package com.misanthropy.collections_of_optimizations.mixin.saintsdragons;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

@Pseudo
@Mixin(targets = "com.leon.saintsdragons.client.camera.IgnivorusSkyfallScreenEffects", remap = false)
public abstract class MixinIgnivorusSkyfallScan {

    @Unique
    private static final List<Entity> coo$dragons = new ArrayList<>();

    @Unique
    private static WeakReference<ClientLevel> coo$scanned = new WeakReference<>(null);

    @Unique
    private static long coo$stamp = Long.MIN_VALUE;

    @Unique
    private static Class<?> coo$dragonClass;

    @Redirect(
            method = "sample",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/ClientLevel;entitiesForRendering()Ljava/lang/Iterable;",
                    remap = true
            ),
            require = 0
    )
    private static Iterable<Entity> coo$dragonsOnly(ClientLevel level) {
        if (!CoOConfig.saintsdragonsCacheSkyfallScan) {
            return level.entitiesForRendering();
        }
        long now = level.getGameTime();
        if (coo$stamp != now || coo$scanned.get() != level) {
            coo$stamp = now;
            coo$scanned = new WeakReference<>(level);
            coo$dragons.clear();
            Class<?> dragon = coo$dragonClass;
            if (dragon == null) {
                try {
                    dragon = Class.forName("com.leon.saintsdragons.server.entity.dragons.ignivorus.Ignivorus", false, Entity.class.getClassLoader());
                } catch (ClassNotFoundException e) {
                    return level.entitiesForRendering();
                }
                coo$dragonClass = dragon;
            }
            for (Entity entity : level.entitiesForRendering()) {
                if (dragon.isInstance(entity)) {
                    coo$dragons.add(entity);
                }
            }
        } else if (!coo$dragons.isEmpty()) {
            coo$dragons.removeIf(Entity::isRemoved);
        }
        return coo$dragons;
    }
}
