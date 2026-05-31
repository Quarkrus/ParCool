package com.alrex.parcool.client.animation.system;

import com.alrex.parcool.client.animation.system.data.AnimationComponentGroup;
import com.alrex.parcool.client.animation.system.data.IAnimationComponent;
import com.alrex.parcool.client.animation.system.data.Transform;
import com.alrex.parcool.client.animation.system.registration.AnimationProgresses;
import net.minecraft.client.player.AbstractClientPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.List;

public class WorkingAnimation implements IWorkingAnimation {
    private static final Logger log = LoggerFactory.getLogger(WorkingAnimation.class);

    public record Component(IAnimationComponent component, @Nullable IBlendingFactor blendingFactor,
                            IAnimationProgress progress) {
    }

    private final List<Component> components;
    private int tick = 0;
    private final int duration;
    private final boolean loop;
    private boolean finished = false;

    public WorkingAnimation(AnimationComponentGroup group) {
        components = group.components().stream().map(it -> new Component(it.component(), it.blendingFactor(), AnimationProgresses.getNewInstance(it.progressID(), group.loops(), 0f, group.duration()))).toList();
        duration = group.duration();
        loop = group.loops();
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public boolean loops() {
        return loop;
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public void tick(AbstractClientPlayer player) {
        tick++;
        if (tick >= getDuration()) {
            if (loops()) {
                tick = 0;
                for (var component : components) {
                    component.progress().reset();
                }
            } else {
                finished = true;
                tick = getDuration();
            }
            return;
        }
        for (var component : components) {
            component.progress().update(player);
        }
    }

    @Override
    public ModelTransform getTransformation(AbstractClientPlayer player, float partialTick) {
        var map = new EnumMap<AnimatableModelPart, Transform>(AnimatableModelPart.class);
        for (var modelPart : AnimatableModelPart.values()) {
            var transform = Transform.NO_TRANSFORMATION;
            for (var component : components) {
                var componentTransform = component.component.getTransform(player, modelPart, component.progress.getProgress(partialTick));
                if (componentTransform != null) {
                    transform = transform.append(
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
