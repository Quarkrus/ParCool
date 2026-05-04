package com.alrex.parcool.server.limitation;

/// Limitation's priority
///
/// `HIGHER` means higher value is used if two different values are given
///
/// `LOWER` means lower value is used if two different values are given
public enum PriorityOrder {
    NONE, HIGHER, LOWER;

    public <T extends Comparable<T>> T select(T v1, T v2) {
        var comparedValue = v1.compareTo(v2);
        return switch (this) {
            case NONE -> v1;
            case HIGHER -> comparedValue > 0 ? v1 : v2;
            case LOWER -> comparedValue < 0 ? v1 : v2;
        };
    }
}
