package com.alrex.parcool.client.animation.system;

import com.alrex.parcool.api.unstable.animation.AnimationPart;
import com.alrex.parcool.client.animation.system.math.EasingFunctions;
import com.alrex.parcool.client.animation.system.registration.ID;
import net.minecraft.client.player.AbstractClientPlayer;

import javax.annotation.Nullable;
import java.util.EnumMap;

public class Animator {
    private final AnimationSet animationSet;
    private final IAnimationController controller;
    private int tick = 0;
    private int primaryTick = 0;
    private AnimationPhase phase;

    public Animator(AnimationSet animation, IAnimationController controller) {
        this.animationSet = animation;
        this.controller = controller;
        phase = animation.introAnimation() != null ? AnimationPhase.INTRO : AnimationPhase.MAIN;
    }

    public AnimationSet getAnimationSet() {
        return animationSet;
    }

    public void tick(AbstractClientPlayer player) {
        tick++;
        primaryTick++;
        var animation = animationSet.getAnimation(phase);
        if ((phase == AnimationPhase.INTRO || phase == AnimationPhase.MAIN) && !controller.continueAnimation(player)) {
            enter(AnimationPhase.OUTRO);
        } else if (animation != null && animation.getDuration() <= tick) {
            nextPhase();
        }
    }

    @Nullable
    public ModelTransform getTransform(AbstractClientPlayer player, float partialTick) {
        var animation = animationSet.getAnimation(phase);
        return animation != null ? animation.getTransformation(player, tick + partialTick) : null;
    }

    public float getBlendFactor(float partialTick) {
        float tick = primaryTick + partialTick;
        float fadeInTick = animationSet.fadingInDuration();
        return fadeInTick <= tick ? 1f : EasingFunctions.QUAD.easeInOut(tick / fadeInTick);
    }

    public void enter(AnimationPhase phase) {
        if (this.phase == phase) {
            return;
        }
        phase = switch (phase) {
            case INTRO -> animationSet.introAnimation() != null ? AnimationPhase.INTRO : AnimationPhase.MAIN;
            case OUTRO -> animationSet.outroAnimation() != null ? AnimationPhase.OUTRO : AnimationPhase.END;
            default -> phase;
        };
        this.phase = phase;
        this.tick = 0;
    }

    public void nextPhase() {
        enter(switch (phase) {
            case INTRO -> AnimationPhase.MAIN;
            case MAIN -> AnimationPhase.OUTRO;
            case OUTRO, END -> AnimationPhase.END;
        });
    }

    public boolean isFinished() {
        return phase == AnimationPhase.END;
    }
}
