package com.alrex.parcool.server.limitation;

import com.alrex.parcool.common.stamina.StaminaTypeEntry;

import javax.annotation.Nullable;

public interface ILimitation {
    boolean get(ILimitationEntry.Bool entry);

    short get(ILimitationEntry.Int entry);

    float get(ILimitationEntry.Real entry);

    @Nullable
    default StaminaTypeEntry<?> getStaminaType() {
        return null;
    }
}
