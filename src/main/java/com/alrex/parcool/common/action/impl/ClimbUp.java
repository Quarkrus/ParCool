package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.ActionEntry;
import com.alrex.parcool.common.action.ContinuableAction;
import com.alrex.parcool.common.action.IRequestable;
import com.alrex.parcool.util.EasingFunctions;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class ClimbUp extends ContinuableAction implements IRequestable<ClimbUp.RequestContext> {
    private static final int MAX_TICK = 10;
    @Nullable
    private Vec3 startPos = null;
    @Nullable
    private Vec3 destination = null;

    public ClimbUp(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry);
    }

    @Override
    public boolean canStart() {
        return false;
    }

    @Override
    public boolean canStart(RequestContext requestContext) {
        var bb = requestContext.hangState.handBoundingBox();
        var center = bb.getCenter();
        var playerBB = parkourability.player().getBoundingBox();
        destination = new Vec3(center.x, bb.minY, center.z);
        startPos = parkourability.player().position();
        var destinationBB = new AABB(
                center.x - playerBB.getXsize() * 0.6,
                bb.minY,
                center.z - playerBB.getZsize() * 0.6,
                center.x + playerBB.getXsize() * 0.6,
                bb.minY + playerBB.getYsize() * 1.4,
                center.z + playerBB.getZsize() * 0.6
        );

        return parkourability.player().level.noCollision(destinationBB);
    }

    @Override
    public void onStartInLocalClient() {
        final var fStartPos = this.startPos;
        final var fDestination = this.destination;
        final int borderTick = 6;
        if (fStartPos != null && fDestination != null) {
            parkourability.getBehaviorEnforcer().setMarkerEnforceMovePoint(() -> getDoingTick() < MAX_TICK, () -> {
                var tick = this.getDoingTick();
                if (tick < borderTick) {
                    return fStartPos.add(0, (fDestination.y - fStartPos.y) * EasingFunctions.cubicInOut(tick / (double) borderTick), 0);
                } else {
                    tick -= borderTick;
                    var phase = tick / (double) (MAX_TICK - borderTick);
                    var t = EasingFunctions.cubicInOut(phase);
                    return new Vec3(
                            Mth.lerp(t, fStartPos.x, fDestination.x),
                            fDestination.y + 0.25 * phase * (1. - phase),
                            Mth.lerp(t, fStartPos.z, fDestination.z)
                    );
                }
            });
        }

    }

    public record RequestContext(HangOn.HangState hangState) {
    }

    @Override
    public boolean canContinue() {
        return getDoingTick() < MAX_TICK;
    }
}
