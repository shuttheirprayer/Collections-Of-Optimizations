package com.misanthropy.collections_of_optimizations.mixin.curios;

import com.google.common.collect.Multimap;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.CurioCachedModifierHolder;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotAttribute;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;
import top.theillusivec4.curios.common.capability.CurioInventoryCapability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Mixin(value = CurioInventoryCapability.CurioInventoryWrapper.class, remap = false)
public abstract class MixinCurioInventoryCachedPurge implements ICuriosItemHandler {

    @Inject(method = "clearCachedSlotModifiers", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$purgeStaleCachedOnly(CallbackInfo ci) {
        if (!CoOConfig.curiosReconcileCachedSlotModifiers) {
            return;
        }
        ci.cancel();

        Map<String, ICurioStacksHandler> curios = this.getCurios();
        List<ICurioStacksHandler> due = null;
        for (ICurioStacksHandler handler : curios.values()) {
            if (handler.getCachedModifiers().isEmpty()) {
                continue;
            }
            if (handler instanceof CurioCachedModifierHolder holder && holder.coo$skipCachedPurgeOnce()) {
                continue;
            }
            if (due == null) {
                due = new ArrayList<>(2);
            }
            due.add(handler);
        }
        if (due == null) {
            return;
        }

        Map<String, Set<UUID>> live = this.coo$liveSlotModifiers(curios);
        for (ICurioStacksHandler handler : due) {
            Set<UUID> keep = live.get(handler.getIdentifier());
            if (keep != null) {
                handler.getCachedModifiers().removeIf(modifier -> keep.contains(modifier.getId()));
            }
            handler.clearCachedModifiers();
        }
    }

    @Unique
    private Map<String, Set<UUID>> coo$liveSlotModifiers(Map<String, ICurioStacksHandler> curios) {
        Map<String, Set<UUID>> live = new HashMap<>();
        LivingEntity wearer = this.getWearer();
        for (Map.Entry<String, ICurioStacksHandler> entry : curios.entrySet()) {
            ICurioStacksHandler handler = entry.getValue();
            IDynamicStackHandler stacks = handler.getStacks();
            NonNullList<Boolean> renders = handler.getRenders();
            for (int i = 0, slots = stacks.getSlots(); i < slots; i++) {
                ItemStack stack = stacks.getStackInSlot(i);
                if (stack.isEmpty()) {
                    continue;
                }
                SlotContext context = new SlotContext(entry.getKey(), wearer, i, false, renders.size() > i && renders.get(i));
                coo$collect(live, CuriosApi.getAttributeModifiers(context, CuriosApi.getSlotUuid(context), stack));
            }
        }
        if (wearer != null) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                ItemStack stack = wearer.getItemBySlot(slot);
                if (!stack.isEmpty()) {
                    coo$collect(live, stack.getAttributeModifiers(slot));
                }
            }
        }
        return live;
    }

    @Unique
    private static void coo$collect(Map<String, Set<UUID>> live, Multimap<Attribute, AttributeModifier> modifiers) {
        for (Map.Entry<Attribute, AttributeModifier> entry : modifiers.entries()) {
            if (entry.getKey() instanceof SlotAttribute slot) {
                live.computeIfAbsent(slot.getIdentifier(), key -> new HashSet<>(4)).add(entry.getValue().getId());
            }
        }
    }
}
