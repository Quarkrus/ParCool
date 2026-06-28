package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.ParCoolAnimations;
import com.alrex.parcool.client.animation.system.PlayerAnimator;
import com.alrex.parcool.client.input.LogicalMovement;
import com.alrex.parcool.client.input.ParCoolKeyBinds;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.*;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class Dodge extends ContinuableAction implements ActionExtension.AttackedListener {
    private final SynchronizedDataHolder dataHolder;
    private final SynchronizedProperty<AnimationType> propertyAnimationType;

    public Dodge(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry);
        dataHolder = SynchronizedDataHolder.create(entry,
                propertyAnimationType = SynchronizedProperty.newEnum(AnimationType.class)
        );
    }

    @Override
    public SynchronizedDataHolder getSynchronizedData() {
        return dataHolder;
    }

    @Override
    public boolean canStart() {
        if (ParCoolKeyBinds.DODGE.state().isJustPressed()) {
            AnimationType type = null;
            if (ParCoolKeyBinds.getMovementInput(LogicalMovement.BACKWARD).isDown()) {
                type = AnimationType.BACK;
            } else if (ParCoolKeyBinds.getMovementInput(LogicalMovement.FORWARD).isDown()) {
                type = AnimationType.FRONT;
            } else if (ParCoolKeyBinds.getMovementInput(LogicalMovement.RIGHT).isDown()) {
                type = AnimationType.RIGHT;
            } else if (ParCoolKeyBinds.getMovementInput(LogicalMovement.LEFT).isDown()) {
                type = AnimationType.LEFT;
            }
            if (type == null) return false;
            propertyAnimationType.set(type);
            return true;
        }
        return false;
    }

    @Override
    public boolean canContinue() {
        return getDoingTick() < 20;
    }

    @Override
    public void onStartInClient() {
        switch (propertyAnimationType.get()) {
            case BACK:
                PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(ParCoolAnimations.DODGE_BACK);
                break;
            case FRONT:
                PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(ParCoolAnimations.DODGE_FRONT);
                break;
            case RIGHT:
                PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(ParCoolAnimations.DODGE_RIGHT);
                break;
            case LEFT:
                PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(ParCoolAnimations.DODGE_RIGHT, true);
                break;
        }
    }

    @Override
    public void onAttacked(LivingAttackEvent event) {
        if (!isDoing()) return;
        if (event.getSource().isBypassArmor()) return;
        if (getDoingTick() < 7) {
            event.setCanceled(true);
        }
    }

    private enum AnimationType {
        BACK, RIGHT, LEFT, FRONT;
    }
}
