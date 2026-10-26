package com.misanthropy.collections_of_optimizations.core;

import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import java.util.function.Consumer;

public final class CreateBehaviourTicks {

    private static final ClassValue<Boolean> TICKS = new ClassValue<>() {
        @Override
        protected Boolean computeValue(Class<?> type) {
            for (Class<?> current = type; current != null && current != BlockEntityBehaviour.class; current = current.getSuperclass()) {
                for (java.lang.reflect.Method method : current.getDeclaredMethods()) {
                    if (method.getParameterCount() == 0 && (method.getName().equals("tick") || method.getName().equals("lazyTick"))) {
                        return true;
                    }
                }
            }
            return false;
        }
    };

    public static final Consumer<BlockEntityBehaviour> TICK_ACTIVE = behaviour -> {
        CreateBehaviourTickFlag cached = (CreateBehaviourTickFlag) behaviour;
        byte flag = cached.coo$tickFlag();
        if (flag == 0) {
            flag = TICKS.get(behaviour.getClass()) ? (byte) 1 : (byte) 2;
            cached.coo$setTickFlag(flag);
        }
        if (flag == 1) {
            behaviour.tick();
        }
    };

    private CreateBehaviourTicks() {
    }
}
