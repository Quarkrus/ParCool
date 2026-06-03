package com.alrex.parcool.client.animation.system.registration;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.client.animation.system.AnimationProgress;
import com.alrex.parcool.client.animation.system.resource.Argument;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.TreeMap;

public class AnimationProgresses {
    private record RegistrationEntry<T extends AnimationProgress>(
            ResourceLocation name,
            AnimationProgress.Constructor<T> constructor
    ) {
    }

    private static final IDProvider<AnimationProgress> idProvider = new IDProvider<>();
    private static final TreeMap<ID<AnimationProgress>, RegistrationEntry<?>> registry = new TreeMap<>();
    private static final TreeMap<ResourceLocation, ID<AnimationProgress>> nameToId = new TreeMap<>();

    public static ID<AnimationProgress> register(String subName, AnimationProgress.IDeltaProgressProvider progressProvider) {
        var name = new ResourceLocation(ParCool.MOD_ID, subName);
        var id = idProvider.newID();
        registry.put(id, new RegistrationEntry<>(
                new ResourceLocation(ParCool.MOD_ID, subName),
                (loop, min, max, args) -> new AnimationProgress.FunctionAnimationProgress(loop, min, max, args, progressProvider)
        ));
        nameToId.put(name, id);
        return id;
    }

    public static ID<AnimationProgress> register(ResourceLocation name, AnimationProgress.IDeltaProgressProvider progressProvider) {
        var id = idProvider.newID();
        registry.put(id, new RegistrationEntry<>(
                name,
                (loop, min, max, args) -> new AnimationProgress.FunctionAnimationProgress(loop, min, max, args, progressProvider)
        ));
        nameToId.put(name, id);
        return id;
    }

    public static ID<AnimationProgress> register(String subName, AnimationProgress.Constructor<?> progressProvider) {
        var name = new ResourceLocation(ParCool.MOD_ID, subName);
        var id = idProvider.newID();
        registry.put(id, new RegistrationEntry<>(new ResourceLocation(ParCool.MOD_ID, subName), progressProvider));
        nameToId.put(name, id);
        return id;
    }

    public static ID<AnimationProgress> register(ResourceLocation name, AnimationProgress.Constructor<?> progressProvider) {
        var id = idProvider.newID();
        registry.put(id, new RegistrationEntry<>(name, progressProvider));
        nameToId.put(name, id);
        return id;
    }

    @Nullable
    public static ID<AnimationProgress> getID(ResourceLocation name) {
        return nameToId.get(name);
    }

    public static AnimationProgress getNewInstance(ID<AnimationProgress> id, boolean loop, float rangeMin, float rangeMax, Argument argument) {
        return registry.get(id).constructor.newInstance(loop, rangeMin, rangeMax, argument);
    }

    public static AnimationProgress getNewInstance(ID<AnimationProgress> id) {
        return registry.get(id).constructor.newInstance(false, 0f, Float.MAX_VALUE, Argument.EMPTY);
    }

    public static final ID<AnimationProgress> TIME = register("time", (player) -> 1);
    public static final ID<AnimationProgress> VELOCITY = register("velocity", (player) -> (float) player.getDeltaMovement().length());
    public static final ID<AnimationProgress> VELOCITY_H = register("velocity_h", (player) -> (float) player.getDeltaMovement().multiply(1, 0, 1).length());
}
