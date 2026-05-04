package com.alrex.parcool.client.animation.system;

import com.alrex.parcool.client.animation.system.math.Vec3f;
import com.mojang.math.Quaternion;
import net.minecraft.client.player.AbstractClientPlayer;

import javax.annotation.Nullable;
import java.util.EnumMap;

public class AnimationComponent {
    public AnimationComponent(EnumMap<AnimatableModelPart, EnumMap<AnimatableProperty, Timeline>> animationCurves) {
        this.animationCurves = animationCurves;
    }

    private final EnumMap<AnimatableModelPart, EnumMap<AnimatableProperty, Timeline>> animationCurves;

    @Nullable
    public Transform getTransform(AnimatableModelPart part, float tick) {
        var curves = animationCurves.get(part);
        if (curves == null) return null;
        var translation = new float[3];
        for (int i = 0; i < 3; i++) {
            var property = AnimatableProperty.TRANSLATIONS.get(i);
            var curve = curves.get(property);
            translation[i] = curve != null
                    ? curve.getValue(tick)
                    : property.getDefaultValue();
        }
        var rotation = new float[4];
        for (int i = 0; i < 4; i++) {
            var property = AnimatableProperty.ROTATIONS.get(i);
            var curve = curves.get(property);
            rotation[i] = curve != null
                    ? curve.getValue(tick)
                    : property.getDefaultValue();
        }

        return new Transform(
                new Vec3f(translation[0], translation[1], translation[2]),
                new Quaternion(rotation[1], rotation[2], rotation[3], rotation[0])
        );
    }
}
