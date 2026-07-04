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
import net.minecraft.client.player.AbstractClientPlayer;

public class TrickJump extends Action implements ActionExtension.JumpListener {
    public enum Type {
        FRONT_FLIP, BACK_FLIP;
    }

    private final SynchronizedDataHolder dataHolder;
    private final SynchronizedProperty<Type> propertyTrickType;
    private boolean jumped;

    public TrickJump(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry);
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
            propertyTrickType.set(Type.BACK_FLIP);
            return true;
        }
        duration = ParCoolKeyBinds.getMovementInput(LogicalMovement.FORWARD).getPressedDuration();
        if (0 <= duration && duration < 5) {
            propertyTrickType.set(Type.FRONT_FLIP);
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
                        case FRONT_FLIP -> ParCoolAnimations.FRONT_FLIP;
                        case BACK_FLIP -> ParCoolAnimations.BACK_FLIP;
                    }
            );
        }
    }
}
