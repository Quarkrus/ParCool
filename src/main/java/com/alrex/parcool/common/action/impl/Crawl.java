package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.ParCoolAnimations;
import com.alrex.parcool.client.animation.system.PlayerAnimator;
import com.alrex.parcool.client.input.ParCoolKeyBinds;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.ActionEntry;
import com.alrex.parcool.common.action.ContinuableAction;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.Pose;

public class Crawl extends ContinuableAction {
    public Crawl(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry);
    }

    @Override
    public boolean canStart() {
        if (parkourability.player().getForcedPose() != null) return false;
        if (ParCoolKeyBinds.CRAWL.key().isDown()) return true;
        return parkourability.player().hasPose(Pose.SWIMMING);
    }

    @Override
    public boolean canContinue() {
        if (ParCoolKeyBinds.CRAWL.key().isDown()) return true;
        return !parkourability.player().canEnterPose(Pose.CROUCHING) && parkourability.player().hasPose(Pose.SWIMMING);
    }

    @Override
    public void onStartInClient() {
        PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(ParCoolAnimations.CRAWL);
    }

    @Override
    public void onStart() {
        parkourability.player().setForcedPose(Pose.SWIMMING);
    }

    @Override
    public void onStop() {
        if (parkourability.player().getForcedPose() == Pose.SWIMMING) {
            parkourability.player().setForcedPose(null);
        }
    }
}
