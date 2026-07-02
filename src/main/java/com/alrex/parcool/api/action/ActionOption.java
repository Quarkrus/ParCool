package com.alrex.parcool.api.action;

import net.minecraft.world.entity.Pose;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;

public class ActionOption {
    public record Value(
            StaminaConsumption defaultCost,
            @Nullable ActionEntry<? extends ContinuableAction> parent,
            @Nullable Pose neededPose,
            boolean needOnGround,
            boolean needNotOnGround,
            boolean needParentWorking,
            boolean availableInFluid,
            boolean availableWithFallFlying,
            LogicalSide triggeredSide
    ) {
    }

    private StaminaConsumption staminaConsumption = StaminaConsumption.ZERO;
    @Nullable
    private ActionEntry<? extends ContinuableAction> parent = null;
    @Nullable
    private Pose neededPose = Pose.STANDING;
    private boolean needParentWorking = false;
    private boolean availableInFluid = false;
    private boolean availableWithFallFlying = false;
    private boolean needOnGround = false;
    private boolean needNotOnGround = false;
    private LogicalSide triggeredSide = LogicalSide.CLIENT;

    public Value build() {
        return new Value(
                staminaConsumption, parent, neededPose, needOnGround, needNotOnGround, needParentWorking, availableInFluid, availableWithFallFlying, triggeredSide
        );
    }

    public ActionOption() {
    }

    public ActionOption cost(StaminaConsumption consumption) {
        this.staminaConsumption = consumption;
        return this;
    }

    public ActionOption parent(ActionEntry<? extends ContinuableAction> parentAction) {
        this.parent = parentAction;
        return this;
    }

    public ActionOption needParentWorking(boolean needParent) {
        this.needParentWorking = needParent;
        return this;
    }

    public ActionOption needPose(@Nullable Pose pose) {
        this.neededPose = pose;
        return this;
    }

    public ActionOption needOnGround(boolean value) {
        this.needOnGround = value;
        return this;
    }

    public ActionOption needNotOnGround(boolean value) {
        this.needNotOnGround = value;
        return this;
    }

    public ActionOption availableInFluid(boolean availableInFluid) {
        this.availableInFluid = availableInFluid;
        return this;
    }

    public ActionOption availableWithFallFlying(boolean availableWithFallFlying) {
        this.availableWithFallFlying = availableWithFallFlying;
        return this;
    }

    public ActionOption triggeredSide(LogicalSide side) {
        this.triggeredSide = side;
        return this;
    }
}
