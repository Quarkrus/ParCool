package com.alrex.parcool.client.animation.system;

import net.minecraft.client.player.AbstractClientPlayer;

public class NullAnimation implements IAnimation {
    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public boolean loops() {
        return false;
    }

    @Override
    public ModelTransform getTransformation(AbstractClientPlayer player, float tick) {
        return ModelTransform.NO_TRANSFORMATION;
    }
}
