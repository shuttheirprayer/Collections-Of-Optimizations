package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class FromTheShadowsMask {

    private static final String MASK_CLASS = "net.sonmok14.fromtheshadows.server.items.PlagueDoctorMaskItem";

    private static final ClassValue<Boolean> MASK_TYPES = new ClassValue<>() {
        @Override
        protected Boolean computeValue(Class<?> type) {
            for (Class<?> current = type; current != null; current = current.getSuperclass()) {
                if (MASK_CLASS.equals(current.getName())) {
                    return Boolean.TRUE;
                }
            }
            return Boolean.FALSE;
        }
    };

    private FromTheShadowsMask() {
    }

    public static boolean mayWearMask(LivingEntity entity) {
        return isMask(entity.getItemBySlot(EquipmentSlot.HEAD))
                || isMask(entity.getItemBySlot(EquipmentSlot.CHEST))
                || isMask(entity.getItemBySlot(EquipmentSlot.LEGS))
                || isMask(entity.getItemBySlot(EquipmentSlot.FEET));
    }

    private static boolean isMask(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        Item item = stack.getItem();
        return MASK_TYPES.get(item.getClass());
    }
}
