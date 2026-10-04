package com.misanthropy.collections_of_optimizations.mixin.lionfishapi;

import com.github.L_Ender.lionfishapi.client.model.AdvancedAnimations.AdvancedAnimationChannel;
import com.github.L_Ender.lionfishapi.client.model.AdvancedAnimations.AdvancedAnimationDefinition;
import com.github.L_Ender.lionfishapi.client.model.AdvancedAnimations.AdvancedKeyframe;
import com.github.L_Ender.lionfishapi.client.model.AdvancedAnimations.AdvancedKeyframeAnimations;
import com.github.L_Ender.lionfishapi.client.model.tools.AdvancedEntityModel;
import com.github.L_Ender.lionfishapi.client.model.tools.AdvancedModelBox;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import net.minecraft.util.Mth;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mixin(value = AdvancedKeyframeAnimations.class, remap = false)
public abstract class MixinLionfishKeyframeAnimations {

    @WrapMethod(method = "animate", require = 0)
    private static void coo$leanAnimate(AdvancedEntityModel<?> model, AdvancedAnimationDefinition definition, long accumulatedTime,
                                        float scale, Vector3f target, Operation<Void> original) {
        if (!CoOConfig.lionfishapiLeanKeyframeAnimation) {
            original.call(model, definition, accumulatedTime, scale, target);
            return;
        }

        float seconds = (float) accumulatedTime / 1000.0F;
        if (definition.looping()) {
            seconds %= definition.lengthInSeconds();
        }

        for (Map.Entry<String, List<AdvancedAnimationChannel>> entry : definition.boneAnimations().entrySet()) {
            Optional<AdvancedModelBox> found = model.getAnyDescendantWithName(entry.getKey());
            if (found.isEmpty()) {
                continue;
            }

            AdvancedModelBox bone = found.get();
            List<AdvancedAnimationChannel> channels = entry.getValue();
            for (int i = 0, channelCount = channels.size(); i < channelCount; i++) {
                AdvancedAnimationChannel channel = channels.get(i);
                AdvancedKeyframe[] keyframes = channel.keyframes();

                int low = 0;
                int span = keyframes.length;
                while (span > 0) {
                    int half = span / 2;
                    int probe = low + half;
                    if (seconds <= keyframes[probe].timestamp()) {
                        span = half;
                    } else {
                        low = probe + 1;
                        span -= half + 1;
                    }
                }

                int from = Math.max(0, low - 1);
                int to = Math.min(keyframes.length - 1, from + 1);
                AdvancedKeyframe start = keyframes[from];
                AdvancedKeyframe end = keyframes[to];
                float progress = to != from
                        ? Mth.clamp((seconds - start.timestamp()) / (end.timestamp() - start.timestamp()), 0.0F, 1.0F)
                        : 0.0F;

                end.interpolation().apply(target, progress, keyframes, from, to, scale);
                channel.target().apply(bone, target);
            }
        }
    }
}
