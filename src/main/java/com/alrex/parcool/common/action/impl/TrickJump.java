package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.ParCoolAnimations;
import com.alrex.parcool.client.animation.system.PlayerAnimator;
import com.alrex.parcool.client.input.LogicalMovement;
import com.alrex.parcool.client.input.ParCoolKeyBinds;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.api.action.Action;
import com.alrex.parcool.api.action.ActionEntry;
import com.alrex.parcool.common.action.ActionExtension;
import net.minecraft.client.player.AbstractClientPlayer;

public class TrickJump extends Action implements ActionExtension.JumpListener {
    private boolean jumped;

    public TrickJump(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry);
    }

    @Override
    public boolean canStart() {
        if (!jumped) return false;
        jumped = false;
        var duration = ParCoolKeyBinds.getMovementInput(LogicalMovement.BACKWARD).getPressedDuration();
        return 0 <= duration && duration < 5;
    }

    @Override
    public void onJump() {
        jumped = true;
    }

    @Override
    public void onStartInClient() {
        PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(ParCoolAnimations.BACK_FLIP);
    }
}
