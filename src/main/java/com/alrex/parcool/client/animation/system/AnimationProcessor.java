package com.alrex.parcool.client.animation.system;

import com.alrex.parcool.client.animation.system.data.AnimationSet;
import com.alrex.parcool.client.animation.system.registration.AnimationSets;
import com.alrex.parcool.client.animation.system.registration.ID;
import com.alrex.parcool.client.animation.system.resource.AnimationResourceManager;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedList;

public class AnimationProcessor {
    private record WorkingAnimationEntry(AnimationSets.Entry registration, WorkingAnimationSet animator) {
    }

    private final ArrayList<WorkingAnimationEntry> animators = new ArrayList<>();
    @Nullable
    private WorkingAnimationSet fadingOutAnimator;
    private int fadingOutTick;
    private int fadingOutTickDuration;

    public void tick(AbstractClientPlayer player) {
        boolean shouldTickFadingOutAnimator = false;
        var finished = new LinkedList<WorkingAnimationEntry>();
        for (var working : animators) {
            if (working.animator == fadingOutAnimator) {
                shouldTickFadingOutAnimator = true;
            }
            working.animator.tick(player);
            if (working.animator.isFinished()) {
                finished.addFirst(working);
            }
        }
        for (var finishedAnim : finished) {
            remove(finishedAnim);
        }
        if (shouldTickFadingOutAnimator) {
            fadingOutAnimator.tick(player);
        }
        if ((++fadingOutTick) >= fadingOutTickDuration) {
            fadingOutAnimator = null;
        }
    }

    public void start(ID<AnimationSet> id) {
        if (!startIfNotWorking(id)) {
            var entry = getWorkingAnimator(id);
            if (entry == null) return;
            entry.animator.enter(AnimationPhase.INTRO);
        }
    }

    public boolean startIfNotWorking(ID<AnimationSet> id) {
        if (isWorking(id)) return false;

        var current = getCurrentAnimation();
        var animEntry = AnimationSets.getInstance().get(id);
        if (animEntry == null) return true;
        var newAnimationSet = AnimationResourceManager.getInstance().getResource().getAnimationSet(id);
        if (newAnimationSet == null) return true;
        if (animEntry.parent() != null) {
            startIfNotWorking(animEntry.parent().id());
        }
        animators.add(new WorkingAnimationEntry(animEntry, new WorkingAnimationSet(newAnimationSet, animEntry.controllerSupplier().get())));
        if (current != null) {
            fadeOut(current.animator, newAnimationSet.fadingInDuration());
        }
        return true;
    }

    private void remove(WorkingAnimationEntry entry) {
        int i = animators.size();
        while ((--i) >= 0) {
            var animation = animators.get(i);
            if (animation == entry) {
                animators.remove(i);
                break;
            }
            if (animation.registration.isDescendantOf(entry.registration.id())) {
                animators.remove(i);
            }
        }
    }
    public void stop(ID<AnimationSet> id) {
        var currentAnimation = getCurrentAnimation();
        int i = animators.size();
        boolean currentAnimationWasRemoved = false;
        while ((--i) >= 0) {
            var animation = animators.get(i);
            if (animation.registration.id() == id) {
                animators.remove(i);
                if (currentAnimation == animation) currentAnimationWasRemoved = true;
                break;
            }
            if (animation.registration.isDescendantOf(id)) {
                animators.remove(i);
                if (currentAnimation == animation) currentAnimationWasRemoved = true;
            }
        }
        if (currentAnimationWasRemoved) {
            var newAnimation = getCurrentAnimation();
            if (currentAnimation.animator.getOutroAnimation() != null) {
                currentAnimation.animator.enter(AnimationPhase.OUTRO);
            }
            fadeOut(currentAnimation.animator, newAnimation != null ? (int) newAnimation.animator.getFadeInTick() : 5);
        }
    }

    @Nullable
    private WorkingAnimationEntry getCurrentAnimation() {
        if (animators.isEmpty()) return null;
        return animators.get(animators.size() - 1);
    }

    @Nullable
    public WorkingAnimationSet getCurrentAnimator() {
        if (animators.isEmpty()) return null;
        return animators.get(animators.size() - 1).animator();
    }

    @Nullable
    public WorkingAnimationSet getFadingOutAnimator() {
        return fadingOutAnimator;
    }

    public float getFadeOutBlendFactor(float partialTick) {
        return Mth.clamp((fadingOutTick + partialTick) / fadingOutTickDuration, 0, 1);
    }

    private void fadeOut(WorkingAnimationSet animator, int fadingOutDurationTick) {
        fadingOutAnimator = animator;
        fadingOutTick = 0;
        fadingOutTickDuration = fadingOutDurationTick;
    }

    @Nullable
    private WorkingAnimationEntry getWorkingAnimator(ID<AnimationSet> id) {
        for (var animator : animators) {
            if (animator.registration.id() == id) return animator;
        }
        return null;
    }

    private boolean isWorking(ID<AnimationSet> id) {
        return getWorkingAnimator(id) != null;
    }
}
