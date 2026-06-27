package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.ParCoolAnimations;
import com.alrex.parcool.client.animation.system.PlayerAnimator;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.ActionEntry;
import com.alrex.parcool.common.action.BehaviorEnforcer;
import com.alrex.parcool.common.action.ContinuableAction;
import com.alrex.parcool.server.limitation.LimitationEntries;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

public class FastRun extends ContinuableAction {
    private static final String FAST_RUNNING_MODIFIER_NAME = "parcool.modifier.fast_run";
    private static final UUID FAST_RUNNING_MODIFIER_UUID = UUID.randomUUID();
    private static final BehaviorEnforcer.ID ENFORCE_SPRINT_ID = BehaviorEnforcer.newID();

    public FastRun(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry);
    }

    @Override
    public boolean canStart() {
        return parkourability.player().isSprinting()
                && Minecraft.getInstance().options.keySprint.isDown();
    }

    @Override
    public boolean canContinue() {
        return parkourability.player().isSprinting()
                && ((LocalPlayer) parkourability.player()).input.hasForwardImpulse()
                && Minecraft.getInstance().options.keySprint.isDown();
    }

    @Override
    public void onStartInClient() {
        PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(ParCoolAnimations.FAST_RUN);
    }

    @Override
    public void onStartInLocalClient() {
        parkourability.getBehaviorEnforcer().addMarkerEnforceSprint(ENFORCE_SPRINT_ID, this::isDoing);
    }

    @Override
    public void onServerTick() {
        var player = parkourability.player();
        var attr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr == null) return;
        var modifier = attr.getModifier(FAST_RUNNING_MODIFIER_UUID);
        if (isDoing()) {
            if (modifier == null) {
                attr.addTransientModifier(new AttributeModifier(
                        FAST_RUNNING_MODIFIER_UUID,
                        FAST_RUNNING_MODIFIER_NAME,
                        parkourability.getLimitedValue(LimitationEntries.Real.FASTRUN_SPEED_MODIFIER),
                        AttributeModifier.Operation.ADDITION
                ));
            }
        } else {
            if (modifier != null) {
                attr.removeModifier(FAST_RUNNING_MODIFIER_UUID);
            }
        }
    }
}
