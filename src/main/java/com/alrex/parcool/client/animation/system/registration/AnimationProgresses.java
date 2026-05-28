package com.alrex.parcool.client.animation.system.registration;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.client.animation.system.IAnimationProgress;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.TreeMap;

public class AnimationProgresses {
    private record RegistrationEntry<T extends IAnimationProgress>(ResourceLocation name,
                                                                   IAnimationProgress.Constructor<T> constructor) {
    }

    private static final IDProvider<IAnimationProgress> idProvider = new IDProvider<>();
    private static final TreeMap<ID<IAnimationProgress>, RegistrationEntry<?>> registry = new TreeMap<>();
    private static final TreeMap<ResourceLocation, ID<IAnimationProgress>> nameToId = new TreeMap<>();

    public static ID<IAnimationProgress> register(String subName, IAnimationProgress.IDeltaProgressProvider progressProvider) {
        var name = new ResourceLocation(ParCool.MOD_ID, subName);
        var id = idProvider.newID();
        registry.put(id, new RegistrationEntry<>(new ResourceLocation(ParCool.MOD_ID, subName), (loop, min, max) -> new IAnimationProgress.FunctionAnimationProgress(loop, min, max, progressProvider)));
        nameToId.put(name, id);
        return id;
    }

    public static ID<IAnimationProgress> register(ResourceLocation name, IAnimationProgress.IDeltaProgressProvider progressProvider) {
        var id = idProvider.newID();
        registry.put(id, new RegistrationEntry<>(name, (loop, min, max) -> new IAnimationProgress.FunctionAnimationProgress(loop, min, max, progressProvider)));
        nameToId.put(name, id);
        return id;
    }

    @Nullable
    public static ID<IAnimationProgress> getID(ResourceLocation name) {
        return nameToId.get(name);
    }

    public static IAnimationProgress getNewInstance(ID<IAnimationProgress> id, boolean loop, float rangeMin, float rangeMax) {
        return registry.get(id).constructor.newInstance(loop, rangeMin, rangeMax);
    }

    public static IAnimationProgress getNewInstance(ID<IAnimationProgress> id) {
        return registry.get(id).constructor.newInstance(false, 0f, Float.MAX_VALUE);
    }

    public static final ID<IAnimationProgress> TIME = register("time", (player) -> 1);

    public static final ID<IAnimationProgress> VELOCITY = register("velocity", (player) -> (float) player.getDeltaMovement().length());
}
