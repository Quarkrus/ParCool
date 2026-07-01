package com.alrex.parcool.api.stamina;

public interface IReadonlyStamina {
    int max();

    int value();

    boolean isExhausted();

    boolean imposePenalty();
}
