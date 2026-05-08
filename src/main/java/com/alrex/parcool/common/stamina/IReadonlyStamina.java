package com.alrex.parcool.common.stamina;

public interface IReadonlyStamina {
    int max();

    int value();

    boolean isExhausted();

    boolean imposePenalty();
}
