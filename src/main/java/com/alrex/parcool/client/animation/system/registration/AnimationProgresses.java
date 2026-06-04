package com.alrex.parcool.client.animation.system.registration;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.client.animation.system.AnimationProgress;
import com.alrex.parcool.client.animation.system.resource.Argument;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public class AnimationProgresses extends AnimationRegistry<AnimationProgress, AnimationProgresses.RegistrationEntry<?>> {
    public record RegistrationEntry<T extends AnimationProgress>(
            ResourceLocation name,
            AnimationProgress.Constructor<T> constructor
    ) {
    }

    private AnimationProgresses() {
    }

    @Nullable
    private static AnimationProgresses INSTANCE = null;

    public static AnimationProgresses getInstance() {
        if (INSTANCE == null) INSTANCE = new AnimationProgresses();
        return INSTANCE;
    }

    public ID<AnimationProgress> register(String subName, AnimationProgress.IDeltaProgressProvider progressProvider) {
        return this.register(subName, (loop, min, max, args) -> new AnimationProgress.FunctionAnimationProgress(loop, min, max, args, progressProvider));
    }

    public ID<AnimationProgress> register(ResourceLocation name, AnimationProgress.IDeltaProgressProvider progressProvider) {
        return register(name, (loop, min, max, args) -> new AnimationProgress.FunctionAnimationProgress(loop, min, max, args, progressProvider));
    }

    public ID<AnimationProgress> register(String subName, AnimationProgress.Constructor<?> progressProvider) {
        return register(subName, new RegistrationEntry<>(new ResourceLocation(ParCool.MOD_ID, subName), progressProvider));
    }

    public ID<AnimationProgress> register(ResourceLocation name, AnimationProgress.Constructor<?> progressProvider) {
        return register(name, new RegistrationEntry<>(name, progressProvider));
    }

    public AnimationProgress getNewInstance(ID<AnimationProgress> id, boolean loop, float rangeMin, float rangeMax, Argument argument) {
        return getRegistry().get(id).constructor.newInstance(loop, rangeMin, rangeMax, argument);
    }

    public AnimationProgress getNewInstance(ID<AnimationProgress> id) {
        return getRegistry().get(id).constructor.newInstance(false, 0f, Float.MAX_VALUE, Argument.EMPTY);
    }

    public final ID<AnimationProgress> TIME = register("time", (player) -> 1);
    public final ID<AnimationProgress> VELOCITY = register("velocity", (player) -> (float) player.getDeltaMovement().length());
    public final ID<AnimationProgress> VELOCITY_H = register("velocity_h", (player) -> (float) player.getDeltaMovement().multiply(1, 0, 1).length());
}
