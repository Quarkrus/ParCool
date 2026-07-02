package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.action.*;
import com.alrex.parcool.client.animation.ParCoolAnimations;
import com.alrex.parcool.client.animation.system.PlayerAnimator;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ActionExtension;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;

public class Dive extends ContinuableAction implements ActionExtension.JumpListener {
    private final SynchronizedDataHolder dataHolder;
    private final SynchronizedProperty<Float> propertyYSpeedOnBeginning;

    // Only for client
    private float ySpeedO;
    private float ySpeed;
    private boolean jumped;

    public Dive(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry);
        dataHolder = SynchronizedDataHolder.create(entry,
                propertyYSpeedOnBeginning = SynchronizedProperty.newFloat()
        );
    }

    public float getAnimationProgress(float partial) {
        final var offset = -2. / (1. + 1. / Math.E) + 0.5;
        var ySpeedOnBeginning = propertyYSpeedOnBeginning.get();
        if (ySpeedOnBeginning == null || ySpeedOnBeginning <= 0) return 1;
        var yDeltaMovement = Mth.lerp(partial, ySpeedO, ySpeed);
        return (float) Math.max(0, 2. / (1. + Math.exp(yDeltaMovement / ySpeedOnBeginning - 1.)) + offset);
    }

    @Override
    public SynchronizedDataHolder getSynchronizedData() {
        return dataHolder;
    }

    @Override
    public boolean canContinue() {
        return (ySpeed <= ySpeedO + 1e-4);
    }

    @Override
    public void onWorkingTickInClient() {
        ySpeedO = ySpeed;
        ySpeed = (float) (parkourability.player().position().y - parkourability.player().yo);
    }

    @Override
    public boolean canStart() {
        if (!jumped) return false;
        jumped = false;
        if (!parkourability.player().isSprinting()) return false;
        propertyYSpeedOnBeginning.set(ySpeed = ySpeedO = (float) (parkourability.player().position().y - parkourability.player().yo));
        return true;
    }

    @Override
    public void onStartInClient() {
        PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(ParCoolAnimations.DIVE);
    }

    @Override
    public void onJump() {
        jumped = true;
    }
}
