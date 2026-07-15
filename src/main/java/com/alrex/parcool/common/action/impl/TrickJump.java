package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.action.Action;
import com.alrex.parcool.api.action.ActionEntry;
import com.alrex.parcool.api.action.SynchronizedDataHolder;
import com.alrex.parcool.api.action.SynchronizedProperty;
import com.alrex.parcool.client.animation.ParCoolAnimations;
import com.alrex.parcool.client.animation.system.PlayerAnimator;
import com.alrex.parcool.client.input.LogicalMovement;
import com.alrex.parcool.client.input.ParCoolKeyBinds;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ActionExtension;
import com.alrex.parcool.common.action.ParCoolActions;
import net.minecraft.client.player.AbstractClientPlayer;

import java.util.List;

public class TrickJump extends Action implements ActionExtension.JumpListener {
    public enum Type {
        FORWARD, BACK;
    }

    private final SynchronizedDataHolder dataHolder;
    private final SynchronizedProperty<Type> propertyTrickType;
    private boolean jumped;

    public TrickJump(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry, List.of(ParCoolActions.DIVE));
        dataHolder = SynchronizedDataHolder.create(entry,
                propertyTrickType = SynchronizedProperty.newEnum(Type.class)
        );
    }

    @Override
    public boolean canStart() {
        if (!jumped) return false;
        jumped = false;

        var duration = ParCoolKeyBinds.getMovementInput(LogicalMovement.BACKWARD).getPressedDuration();
        if (0 <= duration && duration < 5) {
            propertyTrickType.set(Type.BACK);
            return true;
        }
        if (parkourability.get(ParCoolActions.FAST_RUN).isDoing() && parkourability.player().getRandom().nextInt(8) != 0) {
            propertyTrickType.set(Type.FORWARD);
            return true;
        }
        duration = ParCoolKeyBinds.getMovementInput(LogicalMovement.FORWARD).getPressedDuration();
        if (0 <= duration && duration < 5) {
            propertyTrickType.set(Type.FORWARD);
            return true;
        }
        return false;
    }

    @Override
    public void onJump() {
        jumped = true;
    }

    @Override
    public void onStartInClient() {
        var type = propertyTrickType.get();
        if (type != null) {
            PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(
                    switch (type) {
                        case FORWARD -> ParCoolAnimations.TRICK_JUMP_FORWARD;
                        case BACK -> ParCoolAnimations.TRICK_JUMP_BACK;
                    }
            );
        }
    }
}
