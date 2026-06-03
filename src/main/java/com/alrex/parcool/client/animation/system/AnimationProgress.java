package com.alrex.parcool.client.animation.system;

import com.alrex.parcool.client.animation.system.resource.Argument;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;

public abstract class AnimationProgress {
    protected AnimationProgress(boolean loop, float rangeMin, float rangeMax) {
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

    protected AnimationProgress() {
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

    void tick(AbstractClientPlayer player) {
        if (loop) {
            oldProgress = progress - rangeMin - range * Mth.floor((progress - rangeMin) / range);
            progress = oldProgress + getDeltaProgress(player);
        } else {
            oldProgress = progress;
            progress += getDeltaProgress(player);
            progress = Mth.clamp(progress, rangeMin, rangeMax);
        }
    }

    float getProgress(float partialTick) {
        if (loop) {
            var progressWithPartial = Mth.lerp(partialTick, this.oldProgress, this.progress);
            return progressWithPartial - rangeMin - range * Mth.floor((progressWithPartial - rangeMin) / range);
        } else {
            return Mth.lerp(partialTick, oldProgress, progress);
        }
    }

    void reset() {
        progress = 0;
    }

    public interface Constructor<T extends AnimationProgress> {
        T newInstance(boolean loop, float rangeMin, float rangeMax, Argument args);
    }

    public interface IDeltaProgressProvider {
        float get(AbstractClientPlayer player);
    }

    public static class FunctionAnimationProgress extends AnimationProgress {
        private final IDeltaProgressProvider progressProvider;
        private final float scale;

        public FunctionAnimationProgress(boolean loop, float rangeMin, float rangeMax, Argument args, IDeltaProgressProvider deltaProgressProvider) {
            super(loop, rangeMin, rangeMax);
            scale = args.request("scale", 1f);
            progressProvider = deltaProgressProvider;
        }

        public FunctionAnimationProgress(IDeltaProgressProvider deltaProgressProvider) {
            super();
            scale = 1f;
            progressProvider = deltaProgressProvider;
        }

        @Override
        protected float getDeltaProgress(AbstractClientPlayer player) {
            return progressProvider.get(player) * scale;
        }
    }
}
