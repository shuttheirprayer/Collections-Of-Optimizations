package com.misanthropy.collections_of_optimizations.mixin.jerotes;

import com.jerotes.jerotes.entity.Interface.FactionEntity;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.EntityFactionFind;
import com.jerotes.jerotes.util.Main;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = EntityFactionFind.class, remap = false)
public abstract class MixinEntityFactionFindTrueFaction {

    @Inject(method = "getTrueFaction", at = @At("HEAD"), cancellable = true, require = 0)
    private static void coo$trueFactionOnce(LivingEntity entity, CallbackInfoReturnable<String> cir) {
        if (!CoOConfig.jerotesTrueFactionOnce || entity == null) {
            return;
        }
        LivingEntity owner = AttackFind.getOwnerTrue(entity);
        if (owner != null) {
            cir.setReturnValue(EntityFactionFind.getTrueFaction(owner));
            return;
        }
        ListTag marked = Main.getJerotesPersistentData(entity).getList("jerotes_mob_faction", Tag.TAG_STRING);
        if (!marked.isEmpty()) {
            cir.setReturnValue(marked.getString(0));
            return;
        }
        String first = entity instanceof FactionEntity faction ? faction.getFirstFactionTypeName() : "";
        if (!first.isEmpty()) {
            cir.setReturnValue(first);
            return;
        }
        List<String> all = FactionEntity.getFactionTypeListAll(entity);
        cir.setReturnValue(all.isEmpty() ? "" : all.get(0));
    }
}
