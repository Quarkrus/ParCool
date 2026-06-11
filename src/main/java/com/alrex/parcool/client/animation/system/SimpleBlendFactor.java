package com.alrex.parcool.client.animation.system;

import net.minecraft.client.player.AbstractClientPlayer;

public class SimpleBlendFactor implements IBlendingFactor {
    private final Handler handler;
    private final BlendMethod method;

    public SimpleBlendFactor(Handler handler, BlendMethod method) {
        this.handler = handler;
        this.method = method;
    }

    @Override
    public float getFactor(AbstractClientPlayer player, float partial) {
        return handler.getFactor(player, partial);
    }

    @Override
    public void tick() {
    }

    @Override
    public BlendMethod getBlendMethod() {
        return method;
    }

    public interface Handler {
        float getFactor(AbstractClientPlayer player, float partial);
    }
}
