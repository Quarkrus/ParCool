package com.alrex.parcool.client.animation.system;

import com.alrex.parcool.client.animation.system.data.Transform;

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

    public ModelTransform mirror() {
        var newMap = new EnumMap<AnimatableModelPart, Transform>(AnimatableModelPart.class);
        var headTransform = transforms.get(AnimatableModelPart.HEAD);
        if (headTransform != null) {
            newMap.put(AnimatableModelPart.HEAD, headTransform.mirror());
        }
        var bodyTransform = transforms.get(AnimatableModelPart.HEAD);
        if (bodyTransform != null) {
            newMap.put(AnimatableModelPart.HEAD, bodyTransform.mirror());
        }
        var rArmTransform = transforms.get(AnimatableModelPart.RIGHT_ARM);
        var lArmTransform = transforms.get(AnimatableModelPart.LEFT_ARM);
        if (lArmTransform != null) newMap.put(AnimatableModelPart.RIGHT_ARM, lArmTransform);
        if (rArmTransform != null) newMap.put(AnimatableModelPart.LEFT_ARM, rArmTransform);
        var rLegTransform = transforms.get(AnimatableModelPart.RIGHT_LEG);
        var lLegTransform = transforms.get(AnimatableModelPart.LEFT_LEG);
        if (lLegTransform != null) newMap.put(AnimatableModelPart.RIGHT_LEG, lLegTransform);
        if (rLegTransform != null) newMap.put(AnimatableModelPart.LEFT_LEG, rLegTransform);
        return new ModelTransform(newMap);
    }

    public ModelTransform multiply(float factor) {
        return NO_TRANSFORMATION.morph(this, factor);
    }

    public ModelTransform morph(ModelTransform to, float t) {
        var newMap = new EnumMap<AnimatableModelPart, Transform>(AnimatableModelPart.class);
        for (var part : AnimatableModelPart.values()) {
            newMap.put(part, transforms.get(part).morph(to.transforms.get(part), t));
        }
        return new ModelTransform(newMap);
    }
}
