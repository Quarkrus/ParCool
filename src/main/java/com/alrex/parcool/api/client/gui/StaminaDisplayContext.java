package com.alrex.parcool.api.client.gui;

import com.alrex.parcool.common.stamina.AbstractLocalStamina;

public record StaminaDisplayContext(int value, int maxValue, boolean exhausted, boolean infinite, boolean justFilled) {
    public static final StaminaDisplayContext DEFAULT = new StaminaDisplayContext(1, 1, false, false, false);

    public StaminaDisplayContext next(AbstractLocalStamina stamina) {
        return new StaminaDisplayContext(stamina.value(), stamina.max(), stamina.isExhausted(), stamina.isInfinite(), this.value < this.maxValue && stamina.value() >= stamina.max());
    }
}
