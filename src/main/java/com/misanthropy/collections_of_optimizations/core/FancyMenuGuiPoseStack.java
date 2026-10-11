package com.misanthropy.collections_of_optimizations.core;

import com.mojang.blaze3d.vertex.PoseStack;
import de.keksuccino.fancymenu.util.rendering.RenderRotationUtil;
import de.keksuccino.fancymenu.util.rendering.RenderScaleUtil;
import de.keksuccino.fancymenu.util.rendering.RenderTranslationUtil;
import org.joml.Quaternionf;

import java.util.ArrayDeque;
import java.util.Deque;

public final class FancyMenuGuiPoseStack extends PoseStack {

    private final Deque<Float> scales = new ArrayDeque<>();
    private final Deque<RenderTranslationUtil.TranslationState> translations = new ArrayDeque<>();
    private final Deque<RenderRotationUtil.RotationState> rotations = new ArrayDeque<>();

    public FancyMenuGuiPoseStack() {
        scales.addLast(1.0F);
        translations.addLast(new RenderTranslationUtil.TranslationState());
        rotations.addLast(new RenderRotationUtil.RotationState());
    }

    @Override
    public void pushPose() {
        super.pushPose();
        scales.addLast(scales.getLast());
        RenderTranslationUtil.TranslationState last = translations.getLast();
        RenderTranslationUtil.TranslationState next = new RenderTranslationUtil.TranslationState();
        next.x = last.x;
        next.y = last.y;
        next.z = last.z;
        translations.addLast(next);
        rotations.addLast(new RenderRotationUtil.RotationState(rotations.getLast()));
        publishAll();
    }

    @Override
    public void popPose() {
        super.popPose();
        if (scales.size() > 1) {
            scales.removeLast();
        }
        if (translations.size() > 1) {
            translations.removeLast();
        }
        if (rotations.size() > 1) {
            rotations.removeLast();
        }
        publishAll();
    }

    @Override
    public void scale(float x, float y, float z) {
        super.scale(x, y, z);
        float scale = scales.removeLast() * RenderScaleUtil.getAbsoluteScaleFactor_FancyMenu(x, y, z);
        scales.addLast(scale);
        RenderScaleUtil.setActiveRenderScale_FancyMenu(scale);
    }

    @Override
    public void mulPose(Quaternionf quaternion) {
        super.mulPose(quaternion);
        RenderRotationUtil.RotationState rotation = rotations.getLast();
        rotation.mul(quaternion);
        RenderRotationUtil.setActiveRenderRotation_FancyMenu(rotation);
    }

    @Override
    public void translate(float x, float y, float z) {
        super.translate(x, y, z);
        RenderTranslationUtil.TranslationState translation = translations.getLast();
        float scale = RenderScaleUtil.getCurrentAdditionalRenderScale();
        translation.x += x * scale;
        translation.y += y * scale;
        translation.z += z * scale;
        RenderTranslationUtil.setActiveRenderTranslation_FancyMenu(translation.x, translation.y, translation.z);
    }

    private void publishAll() {
        RenderScaleUtil.setActiveRenderScale_FancyMenu(scales.getLast());
        RenderTranslationUtil.TranslationState translation = translations.getLast();
        RenderTranslationUtil.setActiveRenderTranslation_FancyMenu(translation.x, translation.y, translation.z);
        RenderRotationUtil.setActiveRenderRotation_FancyMenu(rotations.getLast());
    }
}
