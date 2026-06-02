package com.alrex.parcool.client.animation.system.data;

import com.alrex.parcool.client.animation.system.AnimationPhase;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public record AnimationSet(
        ResourceLocation name,
        int fadeInDuration,
        int fadeOutDuration,
        @Nullable AnimationComponentGroup introAnimation,
        AnimationComponentGroup mainAnimation,
        @Nullable AnimationComponentGroup outroAnimation
) {
    @Nullable
    AnimationComponentGroup getAnimation(AnimationPhase phase) {
        return switch (phase) {
            case INTRO -> introAnimation;
            case MAIN -> mainAnimation;
            case OUTRO -> outroAnimation;
            case END -> null;
        };
    }
}
