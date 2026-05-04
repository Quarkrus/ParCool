package com.alrex.parcool.client.animation.system;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.client.animation.system.registration.ID;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public record AnimationSet(
        ResourceLocation name,
        int fadingInDuration,
        @Nullable IAnimation introAnimation,
        IAnimation mainAnimation,
        @Nullable IAnimation outroAnimation
) {
    public static final AnimationSet NONE = new AnimationSet(
            new ResourceLocation(ParCool.MOD_ID), 0, null, new NullAnimation(), null
    );

    @Nullable
    IAnimation getAnimation(AnimationPhase phase) {
        return switch (phase) {
            case INTRO -> introAnimation;
            case MAIN -> mainAnimation;
            case OUTRO -> outroAnimation;
            case END -> null;
        };
    }
}
