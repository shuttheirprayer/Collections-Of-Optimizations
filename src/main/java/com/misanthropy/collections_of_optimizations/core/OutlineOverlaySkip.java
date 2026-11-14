package com.misanthropy.collections_of_optimizations.core;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;

public final class OutlineOverlaySkip {
    private static final Object COLOR_MASK;
    private static final Field MASK_ALPHA;
    private static final Object SCISSOR_MODE;
    private static final Field STATE_ENABLED;

    static {
        Object colorMask = null;
        Field maskAlpha = null;
        Object scissorMode = null;
        Field stateEnabled = null;
        try {
            colorMask = ObfuscationReflectionHelper.findField(GlStateManager.class, "COLOR_MASK").get(null);
            maskAlpha = ObfuscationReflectionHelper.findField(colorMask.getClass(), "f_84611_");
            Object scissor = ObfuscationReflectionHelper.findField(GlStateManager.class, "SCISSOR").get(null);
            scissorMode = ObfuscationReflectionHelper.findField(scissor.getClass(), "f_84732_").get(scissor);
            stateEnabled = ObfuscationReflectionHelper.findField(scissorMode.getClass(), "f_84586_");
            if (maskAlpha.getType() != boolean.class || stateEnabled.getType() != boolean.class) {
                maskAlpha = null;
            }
        } catch (Throwable ignored) {
            maskAlpha = null;
        }
        COLOR_MASK = colorMask;
        MASK_ALPHA = maskAlpha;
        SCISSOR_MODE = scissorMode;
        STATE_ENABLED = stateEnabled;
    }

    private static int framebuffer = -1;
    private static int texture = -1;
    private static boolean untouched;
    private static boolean skipping;

    private OutlineOverlaySkip() {
    }

    public static void cleared(RenderTarget target, float clearAlpha) {
        if (!CoOConfig.vanillaSkipEmptyOutlineOverlay || MASK_ALPHA == null) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null || minecraft.levelRenderer == null || target != minecraft.levelRenderer.entityTarget()) {
            return;
        }
        framebuffer = target.frameBufferId;
        texture = target.getColorTextureId();
        untouched = clearAlpha == 0.0F && clearedEverything();
    }

    private static boolean clearedEverything() {
        try {
            return MASK_ALPHA.getBoolean(COLOR_MASK) && !STATE_ENABLED.getBoolean(SCISSOR_MODE);
        } catch (IllegalAccessException e) {
            return false;
        }
    }

    public static int framebufferBound(int id) {
        if (id == framebuffer) {
            untouched = false;
        }
        return id;
    }

    public static int textureBound(int id) {
        if (id == texture) {
            untouched = false;
        }
        return id;
    }

    public static void beginOverlay() {
        RenderTarget target = Minecraft.getInstance().levelRenderer.entityTarget();
        skipping = untouched && target != null && target.frameBufferId == framebuffer
                && target.getColorTextureId() == texture && !IrisState.shaderPackInUse();
    }

    public static void endOverlay() {
        skipping = false;
        untouched = false;
    }

    public static boolean skipsDraw(RenderTarget target) {
        return skipping && target.frameBufferId == framebuffer;
    }
}
