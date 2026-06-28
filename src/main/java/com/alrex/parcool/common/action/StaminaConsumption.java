package com.alrex.parcool.common.action;

public record StaminaConsumption(short onStart, short onWorking, short onFinish) {
    public final static StaminaConsumption ZERO = new StaminaConsumption((short) 0, (short) 0, (short) 0);
    public static StaminaConsumption get(int onStart, int onWorking, int onFinish) {
        return new StaminaConsumption((short) onStart, (short) onWorking, (short) onFinish);
    }

    public short get(StaminaConsumeType type) {
        return switch (type) {
            case START -> onStart;
            case WORKING -> onWorking;
            case FINISH -> onFinish;
        };
    }
}
