package com.misanthropy.collections_of_optimizations.mixin.architectury;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Mixin(targets = "dev.architectury.event.EventFactory", remap = false)
public abstract class MixinEventFactoryDispatch {

    @SuppressWarnings("unchecked")
    @Overwrite(remap = false)
    private static <T, R> R invokeMethod(T listener, Method method, Object[] args) throws Throwable {
        if (!CoOConfig.architecturyDirectEventDispatch) {
            return (R) MethodHandles.lookup().unreflect(method).bindTo(listener).invokeWithArguments(args);
        }
        try {
            return (R) method.invoke(listener, args);
        } catch (InvocationTargetException exception) {
            throw exception.getCause();
        }
    }
}
