package com.alrex.parcool.client.animation.system;

import com.alrex.parcool.client.animation.system.data.AnimationSet;
import com.alrex.parcool.client.animation.system.registration.ID;
import net.minecraft.client.player.AbstractClientPlayer;

import javax.annotation.Nullable;

public class PlayerAnimator {
    public record BlendingModelTransform(ModelTransform transformation, boolean isOverwriting, float blendFactor) {
        private static BlendingModelTransform from(ModelTransform transform, float blendFactor) {
            return new BlendingModelTransform(transform, Math.abs(blendFactor - 1f) < 1e-6, blendFactor);
        }

        private static BlendingModelTransform from(ModelTransform transform) {
            return new BlendingModelTransform(transform, true, 1f);
        }
    }

    private final AnimationProcessor animationProcessor = new AnimationProcessor();
    @Nullable
    private BlendingModelTransform currentTransformation = null;

    @Nullable
    private BlendingModelTransform getTransform(AbstractClientPlayer player, float partialTick) {
        var currentAnimator = animationProcessor.getCurrentAnimator();
        if (currentAnimator != null) {
            var currentTransform = currentAnimator.getTransform(player, partialTick);
            if (currentTransform == null) return null;
            var fadingOutAnimator = animationProcessor.getFadingOutAnimator();
            var currentAnimationBlendFactor = currentAnimator.getCurrentAnimationBlendFactor(partialTick);
            if (fadingOutAnimator != null) {
                var fadingOutTransform = fadingOutAnimator.getTransform(player, partialTick);
                if (fadingOutTransform != null) {
                    var fadeOutBlendFactor = animationProcessor.getFadeOutAnimationBlendFactor(partialTick);
                    return BlendingModelTransform.from(currentTransform.multiply(currentAnimationBlendFactor).morph(fadingOutTransform, fadeOutBlendFactor));
                }
            }
            return BlendingModelTransform.from(currentTransform, currentAnimationBlendFactor);
        } else {
            var fadingOutAnimator = animationProcessor.getFadingOutAnimator();
            if (fadingOutAnimator != null) {
                var fadingOutTransform = fadingOutAnimator.getTransform(player, partialTick);
                if (fadingOutTransform != null) {
                    var blendFactor = fadingOutAnimator.getCurrentAnimationBlendFactor(partialTick) * animationProcessor.getFadeOutAnimationBlendFactor(partialTick);
                    return BlendingModelTransform.from(fadingOutTransform, blendFactor);
                }
            }
        }
        return null;
    }

    public void tick(AbstractClientPlayer player) {
        animationProcessor.tick(player);
    }

    public void onRenderTick(AbstractClientPlayer player, float partialTick) {
        updateTransformation(player, partialTick);
    }

    private void updateTransformation(AbstractClientPlayer player, float partialTick) {
        currentTransformation = getTransform(player, partialTick);
    }

    @Nullable
    public BlendingModelTransform getCurrentTransformation() {
        return currentTransformation;
    }

    public void start(ID<AnimationSet> id) {
        animationProcessor.start(id);
    }

    public void startIfNotWorking(ID<AnimationSet> id) {
        animationProcessor.startIfNotWorking(id);
    }

    public void stop(ID<AnimationSet> id) {
        animationProcessor.stop(id);
    }

    public void stopImmediately(ID<AnimationSet> id) {
        animationProcessor.stopImmediately(id);
    }
}
