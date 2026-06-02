package com.alrex.parcool.client.animation.system;

public record BlendingModelTransform(ModelTransform transformation, boolean isOverwriting, float blendFactor) {
    public static BlendingModelTransform from(ModelTransform transform, float blendFactor) {
        return new BlendingModelTransform(transform, Math.abs(blendFactor - 1f) < 1e-6, blendFactor);
    }

    public static BlendingModelTransform from(ModelTransform transform) {
        return new BlendingModelTransform(transform, true, 1f);
    }
}
