package com.misanthropy.collections_of_optimizations.core;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.opengl.GL30;

public final class PausedWorldFrame {
    private static final long REDRAW_NANOS = 100_000_000L;

    private static TextureTarget copy;
    private static boolean fresh;
    private static long drawnAt;

    private PausedWorldFrame() {
    }

    public static boolean reuse(Minecraft mc) {
        if (!applies(mc)) {
            release();
            return false;
        }
        RenderTarget main = mc.getMainRenderTarget();
        if (!fresh || copy.width != main.width || copy.height != main.height || System.nanoTime() - drawnAt >= REDRAW_NANOS) {
            return false;
        }
        blit(copy, main);
        main.bindWrite(true);
        return true;
    }

    public static void drawn(Minecraft mc) {
        if (!applies(mc)) {
            return;
        }
        RenderTarget main = mc.getMainRenderTarget();
        if (copy == null) {
            copy = new TextureTarget(main.width, main.height, false, Minecraft.ON_OSX);
        } else if (copy.width != main.width || copy.height != main.height) {
            copy.resize(main.width, main.height, Minecraft.ON_OSX);
        }
        blit(main, copy);
        main.bindWrite(true);
        fresh = true;
        drawnAt = System.nanoTime();
    }

    private static boolean applies(Minecraft mc) {
        if (!CoOConfig.vanillaPausedWorldFrame || !mc.isPaused()) {
            return false;
        }
        Screen screen = mc.screen;
        if (screen instanceof PauseScreen || screen instanceof OptionsScreen || screen instanceof OptionsSubScreen) {
            return true;
        }
        if (screen == null) {
            return false;
        }
        String name = screen.getClass().getSimpleName();
        return name.contains("Options") || name.contains("Settings") || name.contains("Config");
    }

    private static void release() {
        fresh = false;
        if (copy != null) {
            copy.destroyBuffers();
            copy = null;
        }
    }

    private static void blit(RenderTarget from, RenderTarget to) {
        GlStateManager._glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, from.frameBufferId);
        GlStateManager._glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, to.frameBufferId);
        GL30.glBlitFramebuffer(0, 0, from.width, from.height, 0, 0, to.width, to.height, GL30.GL_COLOR_BUFFER_BIT, GL30.GL_NEAREST);
    }
}
