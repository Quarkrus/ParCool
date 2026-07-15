package com.alrex.parcool.api.stamina;

public interface IReadonlyStamina {
    double max();

    double value();

    boolean isExhausted();

    boolean imposePenalty();
}
