package com.alrex.parcool.client.animation.system.data;

import com.alrex.parcool.client.animation.system.math.EasingFunctions;
import com.alrex.parcool.client.animation.system.math.IEasingFunction;
import net.minecraft.util.Mth;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class Transition {
    protected final TimedValue start;

    private Transition(TimedValue startValue) {
        this.start = startValue;
    }

    public TimedValue getStart() {
        return start;
    }

    public final float getValueAt(float tick, @Nullable Transition next) {
        if (next == null) {
            return this.start.value();
        }
        return getValue(Mth.clamp((tick - start.time()) / (next.start.time() - start.time()), 0f, 1f), next.start);
    }

    /// @param t : Special variable, which indices the time between start.time and next.start.time in [0,1]
    protected abstract float getValue(float t, @Nonnull TimedValue end);

    public static class End extends Transition {
        public End(TimedValue startValue) {
            super(startValue);
        }

        @Override
        protected float getValue(float t, @Nonnull TimedValue end) {
            return 0;
        }
    }

    public static class Constant extends Transition {
        public Constant(TimedValue startValue) {
            super(startValue);
        }

        @Override
        protected float getValue(float t, @Nonnull TimedValue end) {
            return start.value();
        }
    }

    public static class Linear extends Transition {
        public Linear(TimedValue startValue) {
            super(startValue);
        }

        @Override
        protected float getValue(float t, @Nonnull TimedValue end) {
            return Mth.lerp(t, start.value(), end.value());
        }
    }

    /// [easing.net](https://easings.net/)
    public static abstract class Easing extends Transition {
        private final Type easingType;
        private final IEasingFunction easingFunction;

        private Easing(TimedValue startValue, Type type, IEasingFunction easingFunction) {
            super(startValue);
            this.easingType = type;
            this.easingFunction = easingFunction;
        }

        public enum Type {
            EASE_IN_OUT, EASE_IN, EASE_OUT
        }

        @Override
        protected float getValue(float t, @Nonnull TimedValue end) {
            return Mth.lerp(
                    switch (easingType) {
                        case EASE_IN_OUT -> easingFunction.easeInOut(t);
                        case EASE_IN -> easingFunction.easeIn(t);
                        case EASE_OUT -> easingFunction.easeOut(t);
                    },
                    start.value(),
                    end.value()
            );
        }
    }

    public static class Sine extends Easing {
        public Sine(TimedValue startValue, Type type) {
            super(startValue, type, EasingFunctions.SINE);
        }
    }

    public static class Quad extends Easing {
        public Quad(TimedValue startValue, Type type) {
            super(startValue, type, EasingFunctions.QUAD);
        }
    }

    public static class Cubic extends Easing {
        public Cubic(TimedValue startValue, Type type) {
            super(startValue, type, EasingFunctions.CUBE);
        }
    }

    public static class Circle extends Easing {
        public Circle(TimedValue startValue, Type type) {
            super(startValue, type, EasingFunctions.CIRCLE);
        }
    }

    public static class BazierCubic extends Transition {
        private final TimedValue firstControlPoint;
        private final TimedValue secondControlPoint;

        public BazierCubic(TimedValue startValue, TimedValue firstControlPoint, TimedValue secondControlPoint) {
            super(startValue);
            this.firstControlPoint = firstControlPoint;
            this.secondControlPoint = secondControlPoint;
        }

        @Override
        protected float getValue(float t, @Nonnull TimedValue end) {
            return 0;
        }
    }
}
