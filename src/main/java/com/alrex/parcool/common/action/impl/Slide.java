package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.action.*;
import com.alrex.parcool.client.animation.ParCoolAnimations;
import com.alrex.parcool.client.animation.system.PlayerAnimator;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.IRequestable;
import com.alrex.parcool.util.EntityUtil;
import com.alrex.parcool.util.VectorUtil;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class Slide extends ContinuableAction implements IRequestable<Slide.RequestContext> {
    private final SynchronizedDataHolder dataHolder;
    private final SynchronizedProperty<Vec3> propertyMovingDirection;
    public Slide(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry);
        dataHolder = SynchronizedDataHolder.create(entry,
                propertyMovingDirection = SynchronizedProperty.newVec3Horizontal()
        );
    }

    @Nullable
    public Vec3 getSlidingDirection() {
        return propertyMovingDirection.get();
    }

    @Override
    public SynchronizedDataHolder getSynchronizedData() {
        return dataHolder;
    }

    @Override
    public boolean canStart() {
        return false;
    }

    @Override
    public boolean canContinue() {
        return getDoingTick() < 20; //TODO : make this configurable
    }

    @Override
    public boolean canStart(RequestContext context) {
        propertyMovingDirection.set(VectorUtil.fromYawDegree(parkourability.player().getYRot()).scale(1.25 * EntityUtil.getHorizontalMaximumSpeed(parkourability.player())));
        return true;
    }

    @Override
    public void onStartInLocalClient() {
        final var movingDirection = propertyMovingDirection.get();
        if (movingDirection == null) return;
        parkourability.getBehaviorEnforcer().setMarkerEnforcingDeltaMovement(
                this::isDoing,
                () -> new Vec3(movingDirection.x, parkourability.player().getDeltaMovement().y, movingDirection.z)
        );
    }

    @Override
    public void onStartInClient() {
        PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(ParCoolAnimations.SLIDE);
    }

    public record RequestContext() {
    }
}
