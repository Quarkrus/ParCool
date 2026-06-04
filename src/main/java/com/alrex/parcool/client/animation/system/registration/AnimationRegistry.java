package com.alrex.parcool.client.animation.system.registration;

import com.alrex.parcool.ParCool;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.TreeMap;

public abstract class AnimationRegistry<T, E> {
    private final IDProvider<T> idProvider = new IDProvider<>();
    private final TreeMap<ID<T>, E> registry = new TreeMap<>();
    private final TreeMap<ResourceLocation, ID<T>> nameToId = new TreeMap<>();

    protected TreeMap<ID<T>, E> getRegistry() {
        return registry;
    }

    public ID<T> register(String subName, E entry) {
        var name = new ResourceLocation(ParCool.MOD_ID, subName);
        return register(name, entry);
    }

    public ID<T> register(ResourceLocation name, E entry) {
        var id = idProvider.newID();
        registry.put(id, entry);
        nameToId.put(name, id);
        return id;
    }

    @Nullable
    public ID<T> getID(ResourceLocation name) {
        return nameToId.get(name);
    }
}
