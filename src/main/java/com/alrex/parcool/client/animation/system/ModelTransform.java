package com.alrex.parcool.client.animation.system;

import java.util.EnumMap;

public record ModelTransform(EnumMap<AnimatableModelPart, Transform> transforms) {
    public static final ModelTransform NO_TRANSFORMATION;

    static {
        var map = new EnumMap<AnimatableModelPart, Transform>(AnimatableModelPart.class);
        for (var part : AnimatableModelPart.values()) {
            map.put(part, Transform.NO_TRANSFORMATION);
        }
        NO_TRANSFORMATION = new ModelTransform(map);
    }

    public ModelTransform morph(ModelTransform to, float t) {
        var newMap = new EnumMap<AnimatableModelPart, Transform>(AnimatableModelPart.class);
        for (var part : AnimatableModelPart.values()) {
            newMap.put(part, transforms.get(part).morph(to.transforms.get(part), t));
        }
        return new ModelTransform(newMap);
    }
}
