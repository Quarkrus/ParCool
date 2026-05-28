package com.alrex.parcool.client.animation.system;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;

public abstract class IAnimationProgress {
    protected IAnimationProgress(boolean loop, float rangeMin, float rangeMax) {
        if (rangeMin < rangeMax) {
            this.rangeMin = rangeMin;
            this.rangeMax = rangeMax;
        } else {
            this.rangeMin = rangeMax;
            this.rangeMax = rangeMin;
        }
        this.range = rangeMax - rangeMin;
        this.loop = loop;
    }

    protected IAnimationProgress() {
        this.rangeMin = 0f;
        this.rangeMax = Float.MAX_VALUE;
        this.range = rangeMax;
        this.loop = false;
    }

    private final boolean loop;
    private final float rangeMin, rangeMax, range;
    private float progress = 0f;
    private float oldProgress = 0f;

    protected abstract float getDeltaProgress(AbstractClientPlayer player);

    void update(AbstractClientPlayer player) {
        oldProgress = progress;
        if (loop) {
            progress = Mth.clamp(getDeltaProgress(player), rangeMin, rangeMax);
        } else {
            progress += getDeltaProgress(player);
            progress = progress - rangeMin - range * Mth.floor((progress - rangeMin) / range);
        }
    }

    public float getProgress() {
        return progress;
    }

    float getProgress(float partialTick) {
        return Mth.lerp(partialTick, oldProgress, progress);
    }

    void reset() {
        progress = 0;
    }

    public interface Constructor<T extends IAnimationProgress> {
        T newInstance(boolean loop, float rangeMin, float rangeMax);
    }

    public interface IDeltaProgressProvider {
        float get(AbstractClientPlayer player);
    }

    public static class FunctionAnimationProgress extends IAnimationProgress {
        private final IDeltaProgressProvider progressProvider;

        public FunctionAnimationProgress(boolean loop, float rangeMin, float rangeMax, IDeltaProgressProvider deltaProgressProvider) {
            super(loop, rangeMin, rangeMax);
            progressProvider = deltaProgressProvider;
        }

        public FunctionAnimationProgress(IDeltaProgressProvider deltaProgressProvider) {
            super();
            progressProvider = deltaProgressProvider;
        }

        @Override
        protected float getDeltaProgress(AbstractClientPlayer player) {
            return progressProvider.get(player);
        }
    }
}
