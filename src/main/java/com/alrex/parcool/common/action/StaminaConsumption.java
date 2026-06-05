package com.alrex.parcool.common.action;

public record StaminaConsumption(short onStart, short onWorking, short onFinish) {
    public static StaminaConsumption get(int onStart, int onWorking, int onFinish) {
        return new StaminaConsumption((short) onStart, (short) onWorking, (short) onFinish);
    }
}
