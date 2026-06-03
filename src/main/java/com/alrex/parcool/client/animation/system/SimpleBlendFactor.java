package com.alrex.parcool.client.animation.system;

import net.minecraft.client.player.AbstractClientPlayer;

public class SimpleBlendFactor implements IBlendingFactor {
    private final Handler handler;

    public SimpleBlendFactor(Handler handler) {
        this.handler = handler;
    }

    @Override
    public float getFactor(AbstractClientPlayer player, float partial) {
        return handler.getFactor(player, partial);
    }

    @Override
    public void tick() {
    }

    public interface Handler {
        float getFactor(AbstractClientPlayer player, float partial);
    }
}
