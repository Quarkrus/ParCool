package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.ParCoolAnimations;
import com.alrex.parcool.client.animation.system.PlayerAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.ActionEntry;
import com.alrex.parcool.common.action.ContinuableAction;
import net.minecraft.client.player.AbstractClientPlayer;

public class Dodge extends ContinuableAction {
    public Dodge(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry);
    }

    @Override
    public boolean canStart() {
        return KeyBindings.getKeyDodge().isDown();
    }

    @Override
    public boolean canContinue() {
        return getDoingTick() < 25;
    }

    @Override
    public void onStartInClient() {
        PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(ParCoolAnimations.DODGE_SIDE);
    }
}
