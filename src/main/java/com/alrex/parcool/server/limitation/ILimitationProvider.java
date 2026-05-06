package com.alrex.parcool.server.limitation;

public interface ILimitationProvider {
    boolean get(ILimitationEntry.Bool entry);

    short get(ILimitationEntry.Int entry);

    double get(ILimitationEntry.Real entry);
}
