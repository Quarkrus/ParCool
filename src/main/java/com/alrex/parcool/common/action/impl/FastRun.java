package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.ParCoolAnimations;
import com.alrex.parcool.client.animation.system.IPlayerAnimatorHolder;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.ActionEntry;
import com.alrex.parcool.common.action.ContinuableAction;
import com.alrex.parcool.server.limitation.LimitationEntries;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

public class FastRun extends ContinuableAction {
    private static final String FAST_RUNNING_MODIFIER_NAME = "parcool.modifier.fast_run";
    private static final UUID FAST_RUNNING_MODIFIER_UUID = UUID.randomUUID();

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
                && Minecraft.getInstance().options.keySprint.isDown();
    }

    @Override
    public void onStartInClient() {
        if (parkourability.player() instanceof IPlayerAnimatorHolder holder) {
            holder.getParCoolPlayerAnimator().start(ParCoolAnimations.FAST_RUN);
        }

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
