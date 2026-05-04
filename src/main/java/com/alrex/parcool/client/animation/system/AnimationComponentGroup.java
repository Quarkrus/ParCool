package com.alrex.parcool.client.animation.system;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.List;

public class AnimationComponentGroup implements IAnimation {
    public record ComponentEntry(AnimationComponent component, @Nullable IBlendingFactor blendingFactor) {
    }

    private final List<ComponentEntry> components;
    private final int duration;
    private final boolean loop;

    public AnimationComponentGroup(List<ComponentEntry> components, int duration, boolean loop) {
        this.components = components;
        this.duration = duration;
        this.loop = loop;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public boolean loops() {
        return loop;
    }

    public ModelTransform getTransformation(AbstractClientPlayer player, float tick) {
        if (loop) {
            tick = tick - duration * Mth.floor(tick / duration);
        } else {
            tick = Mth.clamp(tick, 0, duration);
        }
        var map = new EnumMap<AnimatableModelPart, Transform>(AnimatableModelPart.class);
        for (var modelPart : AnimatableModelPart.values()) {
            var transform = Transform.NO_TRANSFORMATION;
            for (var component : components) {
                var componentTransform = component.component.getTransform(modelPart, tick);
                if (componentTransform != null) {
                    transform = transform.morph(
                            componentTransform,
                            component.blendingFactor != null ? component.blendingFactor.getFactor(player) : 1f
                    );
                }
            }
            if (transform != Transform.NO_TRANSFORMATION) {
                map.put(modelPart, transform);
            }
        }
        return new ModelTransform(map);
    }
}
