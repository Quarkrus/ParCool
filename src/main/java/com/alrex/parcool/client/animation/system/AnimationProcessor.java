package com.alrex.parcool.client.animation.system;

import com.alrex.parcool.client.animation.system.registration.AnimationSets;
import com.alrex.parcool.client.animation.system.registration.ID;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class AnimationProcessor {
    private record WorkingAnimation(AnimationSets.Entry registration, Animator animator) {
    }

    private final ArrayList<WorkingAnimation> animators = new ArrayList<>();
    @Nullable
    private Animator fadingOutAnimator;
    private int fadingOutTick;
    private int fadingOutTickDuration;

    public void tick(AbstractClientPlayer player) {
        boolean shouldTickFadingOutAnimator = false;
        for (var working : animators) {
            if (working.animator == fadingOutAnimator) {
                shouldTickFadingOutAnimator = true;
            }
            working.animator.tick(player);
        }
        if (shouldTickFadingOutAnimator) {
            fadingOutAnimator.tick(player);
        }
        if ((++fadingOutTick) >= fadingOutTickDuration) {
            fadingOutAnimator = null;
        }
    }

    public void start(ID<AnimationSet> id) {
        var entry = getWorkingAnimator(id);
        if (entry != null) {
            entry.animator.enter(AnimationPhase.INTRO);
        } else {
            var current = getCurrentAnimation();
            var animEntry = AnimationSets.getInstance().get(id);
            if (animEntry == null) return;
            if (animEntry.parent() != null) {
                startIfNotWorking(animEntry.parent().id());
            }
            animators.add(new WorkingAnimation(animEntry, new Animator(animEntry.instance(), animEntry.controllerSupplier().get())));
            fadeOut(current.animator, animEntry.instance().fadingInDuration());
        }
    }

    public void startIfNotWorking(ID<AnimationSet> id) {
        if (!isWorking(id)) {
            var animEntry = AnimationSets.getEntry(id);
            if (animEntry == null) return;
            if (animEntry.parent() != null) {
                startIfNotWorking(animEntry.parent().id());
            }
            animators.add(new WorkingAnimation(animEntry, new Animator(animEntry.instance(), animEntry.controllerSupplier().get())));
        }
    }

    public void stop(ID<AnimationSet> id) {
        var currentAnimation = getCurrentAnimation();
        int i = animators.size();
        while ((--i) >= 0) {
            var animation = animators.get(i);
            if (animation.registration.id() == id) {
                animators.remove(i);
                break;
            }
            if (animation.registration.isDescendantOf(id)) {
                animators.remove(i);
            }
        }
        if (currentAnimation.registration.isDescendantOf(id)) {
            var newAnimation = getCurrentAnimation();
            if (currentAnimation.animator.getAnimationSet().outroAnimation() != null) {
                currentAnimation.animator.enter(AnimationPhase.OUTRO);
            }
            fadeOut(currentAnimation.animator, newAnimation != null ? newAnimation.animator.getAnimationSet().fadingInDuration() : 5);
        }
    }

    @Nullable
    private WorkingAnimation getCurrentAnimation() {
        if (animators.isEmpty()) return null;
        return animators.get(animators.size() - 1);
    }

    @Nullable
    public Animator getCurrentAnimator() {
        if (animators.isEmpty()) return null;
        return animators.get(animators.size() - 1).animator();
    }

    @Nullable
    public Animator getFadingOutAnimator() {
        return fadingOutAnimator;
    }

    public float getFadeOutBlendFactor(float partialTick) {
        return Mth.clamp((fadingOutTick + partialTick) / fadingOutTickDuration, 0, 1);
    }

    private void fadeOut(Animator animator, int fadingOutDurationTick) {
        fadingOutAnimator = animator;
        fadingOutTick = 0;
        fadingOutTickDuration = fadingOutDurationTick;
    }

    @Nullable
    private WorkingAnimation getWorkingAnimator(ID<AnimationSet> id) {
        for (var animator : animators) {
            if (animator.registration.id() == id) return animator;
        }
        return null;
    }

    private boolean isWorking(ID<AnimationSet> id) {
        return getWorkingAnimator(id) != null;
    }
}
