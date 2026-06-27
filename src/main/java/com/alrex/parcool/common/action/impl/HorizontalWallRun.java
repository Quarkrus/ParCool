package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.input.ParCoolKeyBinds;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.*;
import com.alrex.parcool.util.VectorUtil;

public class HorizontalWallRun extends ContinuableAction {
    private final SynchronizedDataHolder dataHolder;
    private final SynchronizedProperty<InteractingWallDirection> propertyDirection;

    public HorizontalWallRun(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry);
        dataHolder = SynchronizedDataHolder.create(entry,
                propertyDirection = SynchronizedProperty.newEnum(InteractingWallDirection.class)
        );
    }

    @Override
    public SynchronizedDataHolder getSynchronizedData() {
        return dataHolder;
    }

    @Override
    public boolean canContinue() {
        return propertyDirection.get() != null && ParCoolKeyBinds.HORIZONTAL_WALL_RUN.state().isDown();
    }

    @Override
    public boolean canStart() {
        if (!ParCoolKeyBinds.HORIZONTAL_WALL_RUN.state().isDown() || parkourability.player().isOnGround()) return false;

        var wallDirection = InteractingWallDirection.getAdjacentWall(parkourability.player());
        if (wallDirection == null) return false;
        var lookVec = VectorUtil.fromYawDegree(parkourability.player().getYRot());
        if (Math.abs(lookVec.dot(wallDirection.asVec())) > 0.7071 /* cos(pi/4) */) return false;
        propertyDirection.set(wallDirection);

        return true;
    }

    @Override
    public void onStartInLocalClient() {
        parkourability.getBehaviorEnforcer().setMarkerEnforceDeltaMovement(this::isDoing, () -> {
            var wallDirection = propertyDirection.get();
            if (wallDirection == null) return null;
            return parkourability.player().getDeltaMovement().add(wallDirection.asVec().scale(0.125)).multiply(1, 0, 1);
        });
    }

    @Override
    public void onWorkingTickInLocalClient() {
        propertyDirection.set(InteractingWallDirection.getAdjacentWall(parkourability.player()));
    }
}
