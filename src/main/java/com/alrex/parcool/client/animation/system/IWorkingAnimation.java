package com.alrex.parcool.client.animation.system;

import net.minecraft.client.player.AbstractClientPlayer;

public interface IWorkingAnimation {
    int getDuration();

    boolean loops();

    void tick(AbstractClientPlayer player);

    boolean isFinished();

    ModelTransform getTransformation(AbstractClientPlayer player, float partialTick);
}
