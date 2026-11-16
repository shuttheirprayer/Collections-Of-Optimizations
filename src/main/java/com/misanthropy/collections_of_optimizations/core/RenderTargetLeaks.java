package com.misanthropy.collections_of_optimizations.core;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL30C;

public final class RenderTargetLeaks {

    private static final Logger LOGGER = LogManager.getLogger("collections_of_optimizations");
    private static int freed;

    private RenderTargetLeaks() {
    }

    public static void report(String owner, int width, int height, int framebuffer, int color, int depth) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft != null) {
            minecraft.execute(() -> free(minecraft, owner, width, height, framebuffer, color, depth));
        }
    }

    private static void free(Minecraft minecraft, String owner, int width, int height, int framebuffer, int color, int depth) {
        RenderTarget main = minecraft.getMainRenderTarget();
        if (framebuffer == main.frameBufferId || !GL30C.glIsFramebuffer(framebuffer)) {
            return;
        }
        int boundRead = GL11C.glGetInteger(GL30C.GL_READ_FRAMEBUFFER_BINDING);
        GlStateManager._glBindFramebuffer(GL30C.GL_READ_FRAMEBUFFER, framebuffer);
        int colorType = attachedAs(GL30C.GL_COLOR_ATTACHMENT0, color);
        int depthType = attachedAs(GL30C.GL_DEPTH_ATTACHMENT, depth);
        GlStateManager._glBindFramebuffer(GL30C.GL_READ_FRAMEBUFFER, boundRead);

        if (colorType == GL11C.GL_TEXTURE && color != main.getColorTextureId()) {
            TextureUtil.releaseTextureId(color);
        }
        if (depthType == GL11C.GL_TEXTURE && depth != main.getDepthTextureId()) {
            TextureUtil.releaseTextureId(depth);
        } else if (depthType == GL30C.GL_RENDERBUFFER) {
            GL30C.glDeleteRenderbuffers(depth);
        }
        GlStateManager._glDeleteFramebuffers(framebuffer);

        int count = ++freed;
        if (count <= 8 || Integer.bitCount(count) == 1) {
            LOGGER.warn("Freed leaked render target #{}: {} {}x{}, dropped without destroyBuffers()", count, owner, width, height);
        }
    }

    private static int attachedAs(int point, int name) {
        if (name < 0) {
            return GL11C.GL_NONE;
        }
        int type = GL30C.glGetFramebufferAttachmentParameteri(GL30C.GL_READ_FRAMEBUFFER, point, GL30C.GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE);
        if (type == GL11C.GL_NONE) {
            return GL11C.GL_NONE;
        }
        return GL30C.glGetFramebufferAttachmentParameteri(GL30C.GL_READ_FRAMEBUFFER, point, GL30C.GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME) == name ? type : GL11C.GL_NONE;
    }
}
