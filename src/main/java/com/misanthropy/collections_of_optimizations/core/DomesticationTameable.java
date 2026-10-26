package com.misanthropy.collections_of_optimizations.core;

import com.github.alexthe668.domesticationinnovation.server.entity.ModifedToBeTameable;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.frog.Frog;

public final class DomesticationTameable {

    private static final ClassValue<Boolean> POSSIBLE = new ClassValue<>() {
        @Override
        protected Boolean computeValue(Class<?> type) {
            return Axolotl.class.isAssignableFrom(type)
                    || Fox.class.isAssignableFrom(type)
                    || Rabbit.class.isAssignableFrom(type)
                    || Frog.class.isAssignableFrom(type)
                    || ModifedToBeTameable.class.isAssignableFrom(type)
                    || TamableAnimal.class.isAssignableFrom(type);
        }
    };

    private DomesticationTameable() {
    }

    public static boolean canEverBeTamed(Class<?> type) {
        return POSSIBLE.get(type);
    }
}
