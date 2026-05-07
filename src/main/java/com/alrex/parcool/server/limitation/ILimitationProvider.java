package com.alrex.parcool.server.limitation;

public interface ILimitationProvider {
    boolean get(ILimitationEntry.Bool entry);

    short get(ILimitationEntry.Int entry);

    float get(ILimitationEntry.Real entry);
}
