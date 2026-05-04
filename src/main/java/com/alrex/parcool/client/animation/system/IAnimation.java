package com.alrex.parcool.client.animation.system;

import net.minecraft.client.player.AbstractClientPlayer;

public interface IAnimation {
    int getDuration();

    boolean loops();

    ModelTransform getTransformation(AbstractClientPlayer player, float tick);
}
