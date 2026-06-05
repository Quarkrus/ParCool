package com.alrex.parcool.client.animation.system;

import com.alrex.parcool.client.animation.system.data.AnimationSet;
import com.alrex.parcool.client.animation.system.registration.ID;
import net.minecraft.client.player.AbstractClientPlayer;

import javax.annotation.Nullable;

public class PlayerAnimator {
    public static PlayerAnimator get(AbstractClientPlayer player) {
        return ((IPlayerAnimatorHolder) player).getParCoolPlayerAnimator();
    }
    private final AnimationProcessor animationProcessor = new AnimationProcessor();
    @Nullable
    private BlendingModelTransform currentTransformation = null;

    public void tick(AbstractClientPlayer player) {
        animationProcessor.tick(player);
    }

    public void onRenderTick(AbstractClientPlayer player, float partialTick) {
        updateTransformation(player, partialTick);
    }

    private void updateTransformation(AbstractClientPlayer player, float partialTick) {
        currentTransformation = animationProcessor.getTransformation(player, partialTick);
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
