package com.alrex.parcool.client.animation.system.data;

import com.alrex.parcool.client.animation.system.AnimatableModelPart;
import net.minecraft.client.player.AbstractClientPlayer;

public interface IAnimationComponent {
    int duration();

    boolean loops();

    Transform getTransform(AbstractClientPlayer player, AnimatableModelPart part, float progress);
}
