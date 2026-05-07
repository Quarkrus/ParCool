package com.alrex.parcool.common.stamina;

public interface IReadonlyStamina {
    int getMax();

    int getValue();

    boolean isExhausted();
}
