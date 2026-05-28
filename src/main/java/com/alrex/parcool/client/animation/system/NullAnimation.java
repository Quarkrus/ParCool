package com.alrex.parcool.client.animation.system;

import net.minecraft.client.player.AbstractClientPlayer;

public class NullAnimation implements IWorkingAnimation {
    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public boolean loops() {
        return false;
    }

    @Override
    public void tick(AbstractClientPlayer player) {

    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public ModelTransform getTransformation(AbstractClientPlayer player, float partialTick) {
        return ModelTransform.NO_TRANSFORMATION;
    }
}
