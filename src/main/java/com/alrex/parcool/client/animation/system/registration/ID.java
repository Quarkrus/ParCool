package com.alrex.parcool.client.animation.system.registration;

import javax.annotation.Nonnull;

public class ID<T> implements Comparable<ID<T>> {
    private final int number;

    ID(int number) {
        this.number = number;
    }

    @Override
    public int compareTo(@Nonnull ID o) {
        return Integer.compare(number, o.number);
    }
}
