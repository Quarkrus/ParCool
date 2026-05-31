package com.alrex.parcool.client.animation.system;

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
    private final AnimationProcessor manager = new AnimationProcessor();
    @Nullable
    private BlendingModelTransform currentTransformation = null;

    @Nullable
    private BlendingModelTransform getTransform(AbstractClientPlayer player, float partialTick) {
        var currentAnimator = manager.getCurrentAnimator();
        if (currentAnimator != null) {
            var currentTransform = currentAnimator.getTransform(player, partialTick);
            if (currentTransform == null) return null;
            var fadingOutAnimator = manager.getFadingOutAnimator();
            var fadeInBlendFactor = currentAnimator.getFadeInBlendFactor(partialTick);
            if (fadingOutAnimator != null) {
                var fadingOutTransform = fadingOutAnimator.getTransform(player, partialTick);
                if (fadingOutTransform != null) {
                    var blendFactor = fadeInBlendFactor * manager.getFadeOutBlendFactor(partialTick);
                    return BlendingModelTransform.from(fadingOutTransform.morph(currentTransform, blendFactor));
                }
            }
            return BlendingModelTransform.from(currentTransform, fadeInBlendFactor);
        } else {
            var fadingOutAnimator = manager.getFadingOutAnimator();
            if (fadingOutAnimator != null) {
                var fadingOutTransform = fadingOutAnimator.getTransform(player, partialTick);
                if (fadingOutTransform != null) {
                    var blendFactor = fadingOutAnimator.getFadeInBlendFactor(partialTick) * manager.getFadeOutBlendFactor(partialTick);
                    return BlendingModelTransform.from(fadingOutTransform, blendFactor);
                }
            }
        }
        return null;
    }

    public void tick(AbstractClientPlayer player) {
        manager.tick(player);
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

    public AnimationProcessor getManager() {
        return manager;
    }
}
