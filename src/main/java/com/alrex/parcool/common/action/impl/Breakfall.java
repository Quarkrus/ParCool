package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.action.*;
import com.alrex.parcool.client.animation.ParCoolAnimations;
import com.alrex.parcool.client.animation.system.PlayerAnimator;
import com.alrex.parcool.client.input.LogicalMovement;
import com.alrex.parcool.client.input.ParCoolKeyBinds;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ActionExtension;
import com.alrex.parcool.util.EntityUtil;
import com.alrex.parcool.util.VectorUtil;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingFallEvent;

public class Breakfall extends Action implements ActionExtension.LandListener {
    private final SynchronizedDataHolder holder;
    // Local -> Server
    private final SynchronizedProperty<BreakfallType> propertyInputBreakfallType;
    // Server -> Local
    private final SynchronizedProperty<BreakfallType> propertyWorkingBreakfallType;

    public Breakfall(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry);
        holder = SynchronizedDataHolder.create(entry,
                propertyInputBreakfallType = SynchronizedProperty.newEnum(BreakfallType.class),
                propertyWorkingBreakfallType = SynchronizedProperty.newEnum(BreakfallType.class)
        );
    }

    @Override
    public SynchronizedDataHolder getSynchronizedData() {
        return holder;
    }

    @Override
    public boolean canStart() {
        return false;
    }

    @Override
    public void onTickInLocalClient() {
        propertyInputBreakfallType.set(ParCoolKeyBinds.BREAKFALL.state().isDown() ?
                (ParCoolKeyBinds.getMovementInput(LogicalMovement.FORWARD).isDown() ? BreakfallType.ROLL : BreakfallType.TAP)
                : BreakfallType.NONE
        );
    }

    @Override
    public void onLand(LivingFallEvent event) {
        var breakfallType = propertyInputBreakfallType.get();
        if (breakfallType == null || breakfallType == BreakfallType.NONE) return;
        if (isPossible() && !MinecraftForge.EVENT_BUS.post(new ParCoolActionEvent.TryToStart(parkourability.player(), this))) {
            event.setDamageMultiplier(event.getDamageMultiplier() * 0.5f);
            propertyWorkingBreakfallType.set(breakfallType);

            startExplicitly();
        }
    }

    @Override
    public void onStartInClient() {
        switch (propertyWorkingBreakfallType.getOrDefaultIfNull(BreakfallType.NONE)) {
            case TAP:
                PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(ParCoolAnimations.TAP);
                return;
            case ROLL:
                PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(ParCoolAnimations.ROLL);
        }
    }

    @Override
    public void onStartInLocalClient() {
        switch (propertyWorkingBreakfallType.getOrDefaultIfNull(BreakfallType.NONE)) {
            case TAP:
                parkourability.getBehaviorEnforcer().setMarkerEnforcingDeltaMovement(
                        () -> this.getTickSinceStarted() < 10,
                        () -> parkourability.player().getDeltaMovement().multiply(0, 1, 0)
                );
                return;
            case ROLL:
                var deltaMove = VectorUtil.fromYawDegree(parkourability.player().getYRot()).scale(1.25 * EntityUtil.getHorizontalMaximumSpeed(parkourability.player()));
                parkourability.getBehaviorEnforcer().setMarkerEnforcingDeltaMovement(
                        () -> this.getTickSinceStarted() < 10,
                        () -> new Vec3(deltaMove.x, parkourability.player().getDeltaMovement().y, deltaMove.z)
                );
        }
    }

    public enum BreakfallType {
        NONE, ROLL, TAP
    }
}
