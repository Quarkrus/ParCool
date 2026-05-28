package com.alrex.parcool.client.animation.system;

import com.alrex.parcool.client.animation.system.data.AnimationSet;
import com.alrex.parcool.client.animation.system.math.EasingFunctions;
import net.minecraft.client.player.AbstractClientPlayer;

import javax.annotation.Nullable;

public class WorkingAnimationSet {
    private final IWorkingAnimation mainAnimation;
    @Nullable
    private final IWorkingAnimation introAnimation;
    @Nullable
    private final IWorkingAnimation outroAnimation;
    private final IAnimationController controller;
    private int tick = 0;
    private int primaryTick = 0;
    private final float fadeInTick;
    private AnimationPhase phase;

    public WorkingAnimationSet(AnimationSet animationSet, IAnimationController controller) {
        this.mainAnimation = new WorkingAnimation(animationSet.mainAnimation());
        this.introAnimation = animationSet.introAnimation() != null ? new WorkingAnimation(animationSet.introAnimation()) : null;
        this.outroAnimation = animationSet.outroAnimation() != null ? new WorkingAnimation(animationSet.outroAnimation()) : null;
        this.controller = controller;
        this.fadeInTick = animationSet.fadingInDuration();
        this.phase = animationSet.introAnimation() != null ? AnimationPhase.INTRO : AnimationPhase.MAIN;
    }

    @Nullable
    public IWorkingAnimation getIntroAnimation() {
        return introAnimation;
    }

    @Nullable
    public IWorkingAnimation getOutroAnimation() {
        return outroAnimation;
    }

    public IWorkingAnimation getMainAnimation() {
        return mainAnimation;
    }

    @Nullable
    IWorkingAnimation getAnimation(AnimationPhase phase) {
        return switch (phase) {
            case INTRO -> introAnimation;
            case MAIN -> mainAnimation;
            case OUTRO -> outroAnimation;
            case END -> null;
        };
    }

    public float getFadeInTick() {
        return fadeInTick;
    }

    public void tick(AbstractClientPlayer player) {
        tick++;
        primaryTick++;
        if ((phase == AnimationPhase.INTRO || phase == AnimationPhase.MAIN) && !controller.continueAnimation(player)) {
            enter(AnimationPhase.OUTRO);
        } else {
            var animation = getAnimation(phase);
            if (animation == null) return;
            animation.tick(player);
            if (animation.isFinished()) {
                nextPhase();
            }
        }
    }

    @Nullable
    public ModelTransform getTransform(AbstractClientPlayer player, float partialTick) {
        var animation = getAnimation(phase);
        return animation != null ? animation.getTransformation(player, partialTick) : null;
    }

    public float getFadeInBlendFactor(float partialTick) {
        float tick = primaryTick + partialTick;
        return fadeInTick <= tick ? 1f : EasingFunctions.QUAD.easeInOut(tick / fadeInTick);
    }

    public void enter(AnimationPhase phase) {
        if (this.phase == phase) {
            return;
        }
        phase = switch (phase) {
            case INTRO -> introAnimation != null ? AnimationPhase.INTRO : AnimationPhase.MAIN;
            case OUTRO -> outroAnimation != null ? AnimationPhase.OUTRO : AnimationPhase.END;
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
