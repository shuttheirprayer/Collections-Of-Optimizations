package com.misanthropy.collections_of_optimizations.mixin.peyroscythe;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.List;

@Pseudo
@Mixin(targets = {
        "com.rinko1231.peyroscythe.spellentity.CrimsonMoon",
        "com.rinko1231.peyroscythe.spellentity.holy.MundusEntity",
        "com.rinko1231.peyroscythe.spellentity.holy.GoldenBellEntity"
}, remap = false)
public abstract class MixinPeyroAuraTracking {

    @Inject(
            method = "updateTrackingEntities",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void coo$skipClientAuraScan(CallbackInfo ci) {
        if (!CoOConfig.peyroscytheServerOnlyAuraScans) {
            return;
        }
        Level level = ((Entity) (Object) this).level();
        if (level != null && level.isClientSide) {
            ci.cancel();
        }
    }

    @ModifyExpressionValue(
            method = "m_8119_",
            at = @At(value = "FIELD", target = "trackingEntities:Ljava/util/List;", opcode = Opcodes.GETFIELD),
            require = 0
    )
    private List<Entity> coo$throttleAuraEffects(List<Entity> original) {
        int interval = CoOConfig.peyroscytheAuraEffectApplyInterval;
        if (interval <= 1 || original == null || original.isEmpty()) {
            return original;
        }
        Entity self = (Entity) (Object) this;
        return self.tickCount % interval == 0 ? original : Collections.emptyList();
    }
}
