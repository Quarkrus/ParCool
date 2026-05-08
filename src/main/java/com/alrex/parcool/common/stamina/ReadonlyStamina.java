package com.alrex.parcool.common.stamina;

public record ReadonlyStamina(int value, int max, boolean isExhausted,
                              boolean imposePenalty) implements IReadonlyStamina {
    public static final ReadonlyStamina DEFAULT = new ReadonlyStamina(1, 1, false, false);
}
