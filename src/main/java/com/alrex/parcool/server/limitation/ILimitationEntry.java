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

    default T clampInValidRange(T value) {
        return value;
    }

    record Bool(
            int index,
            String name,
            Boolean defaultValue,
            PriorityOrder priority,
            String description
    ) implements ILimitationEntry<Boolean> {
    }

    record Int(
            int index,
            String name,
            Short defaultValue,
            short min, short max,
            PriorityOrder priority,
            String description
    ) implements ILimitationEntry<Short> {
        @Override
        public Short clampInValidRange(Short value) {
            return (short) Mth.clamp(value, min, max);
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
        @Override
        public Float clampInValidRange(Float value) {
            return Mth.clamp(value, max, max);
        }
    }
}
