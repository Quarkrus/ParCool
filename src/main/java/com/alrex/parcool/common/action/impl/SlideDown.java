package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.input.ParCoolKeyBinds;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.*;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class SlideDown extends ContinuableAction {
    private final SynchronizedDataHolder dataHolder;
    private final SynchronizedProperty<InteractingWallDirection> property_direction;

    public SlideDown(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry, List.of(ParCoolActions.CLIMB_UP, ParCoolActions.HANG_ON));
        var builder = new SynchronizedDataHolder.Builder((byte) 1);
        property_direction = builder.register(() -> SynchronizedProperty.newEnum(InteractingWallDirection.class));
        dataHolder = builder.build(entry);
    }

    @Override
    public SynchronizedDataHolder getSynchronizedData() {
        return dataHolder;
    }

    @Override
    public boolean canContinue() {
        return canStart();
    }

    @Override
    public boolean canStart() {
        if (ParCoolKeyBinds.SLIDE_DOWN.key().isDown() && !parkourability.player().isOnGround()) {
            var direction = InteractingWallDirection.getAdjacentWall(parkourability.player());
            if (direction == null) return false;
            property_direction.set(direction);
            return true;
        }
        return false;
    }

    @Override
    public void onStartInLocalClient() {
        parkourability.getBehaviorEnforcer().setMarkerEnforceMovePoint(
                this::isDoing, () -> {
                    if (!(parkourability.player() instanceof LocalPlayer player)) return null;
                    var direction = property_direction.get();
                    if (direction == null) return null;

                    var speed = player.getSpeed() * 0.2f;
                    var moveVec = player.input.getMoveVector().scale(speed);
                    var actualMoveVec = new Vec3(moveVec.x, 0, moveVec.y).yRot((float) Math.toRadians(-player.getYRot()));
                    if (direction.isProtrusion()) {
                        return parkourability.player().position()
                                .add(new Vec3(
                                        direction.getSignX() > 0 ? Math.max(0, actualMoveVec.x) : Math.min(0, actualMoveVec.x),
                                        player.getDeltaMovement().y,
                                        direction.getSignZ() > 0 ? Math.max(0, actualMoveVec.z) : Math.min(0, actualMoveVec.z)
                                ));
                    } else if (direction.isOblique()) {
                        var directionVec = direction.asVec().yRot(Mth.HALF_PI);
                        return parkourability.player().position()
                                .add(0, player.getDeltaMovement().y, 0)
                                .add(directionVec.scale(directionVec.dot(actualMoveVec)));
                    } else {
                        return parkourability.player().position()
                                .add(direction.getSignX() * 0.2, player.getDeltaMovement().y, direction.getSignZ() * 0.2)
                                .add(actualMoveVec);
                    }
                }
        );
    }

    @Override
    public void onWorkingTickInLocalClient() {
        parkourability.player().setDeltaMovement(parkourability.player().getDeltaMovement().multiply(0, 0.9, 0));
    }

    @Override
    public void onWorkingTickInServer() {
        parkourability.player().fallDistance *= 0.9f;
    }
}
