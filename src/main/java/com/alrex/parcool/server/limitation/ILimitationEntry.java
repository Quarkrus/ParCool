package com.alrex.parcool.server.limitation;


import net.minecraft.util.Mth;

import javax.annotation.Nonnull;

public interface ILimitationEntry<T extends Comparable<T>> {
    String name();

    @Nonnull
    T defaultValue();

    PriorityOrder priority();

    String description();

    int index();

    @Nonnull
    default T clampInValidRange(T value) {
        return value;
    }

    @Nonnull
    T getLowestPriorityValue();

    record Bool(
            int index,
            String name,
            Boolean defaultValue,
            PriorityOrder priority,
            String description
    ) implements ILimitationEntry<Boolean> {
        @Nonnull
        @Override
        public Boolean getLowestPriorityValue() {
            return switch (priority) {
                case HIGHER -> false;
                case LOWER -> true;
                default -> defaultValue();
            };
        }
    }

    record Int(
            int index,
            String name,
            Short defaultValue,
            short min, short max,
            PriorityOrder priority,
            String description
    ) implements ILimitationEntry<Short> {
        @Nonnull
        @Override
        public Short clampInValidRange(Short value) {
            return (short) Mth.clamp(value, min, max);
        }

        @Nonnull
        @Override
        public Short getLowestPriorityValue() {
            return switch (priority) {
                case HIGHER -> min;
                case LOWER -> max;
                default -> defaultValue();
            };
        }
    }

    record Real(
            int index,
            String name,
            Float defaultValue,
            float min, float max,
            PriorityOrder priority,
            String description
    ) implements ILimitationEntry<Float> {
        @Nonnull
        @Override
        public Float clampInValidRange(Float value) {
            return Mth.clamp(value, max, max);
        }

        @Nonnull
        @Override
        public Float getLowestPriorityValue() {
            return switch (priority) {
                case HIGHER -> min;
                case LOWER -> max;
                default -> defaultValue();
            };
        }
    }
}
