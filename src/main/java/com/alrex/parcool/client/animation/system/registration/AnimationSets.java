package com.alrex.parcool.client.animation.system.registration;

import com.alrex.parcool.client.animation.system.IAnimationController;
import com.alrex.parcool.client.animation.system.data.AnimationSet;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.TreeMap;
import java.util.function.Supplier;

public class AnimationSets {
    @Nullable
    private static AnimationSets instance = null;

    public static AnimationSets getInstance() {
        if (instance == null) instance = new AnimationSets();
        return instance;
    }

    public record Entry(ID<AnimationSet> id, ResourceLocation location,
                        Supplier<IAnimationController> controllerSupplier, @Nullable Entry parent) {
        public boolean isDescendantOf(ID<AnimationSet> otherId) {
            Entry ancestor = this.parent;
            while (ancestor != null) {
                if (ancestor.id == otherId) return true;
                ancestor = ancestor.parent;
            }
            return false;
        }
    }

    private boolean freeze = false;
    private final IDProvider<AnimationSet> idProvider = new IDProvider<>();
    private final TreeMap<ID<AnimationSet>, Entry> registry = new TreeMap<>();
    private final TreeMap<ResourceLocation, Entry> nameToRegistry = new TreeMap<>();

    public ID<AnimationSet> register(ResourceLocation location, Supplier<IAnimationController> controllerSupplier, @Nullable ID<AnimationSet> parent) {
        if (freeze) {
            throw new IllegalStateException(String.format("Animation set [%s] is tried to be registered, into freezed registry", location));
        }
        var id = idProvider.newID();
        Entry parentEntry = null;
        if (parent != null) {
            parentEntry = get(parent);
        }
        var entry = new Entry(id, location, controllerSupplier, parentEntry);
        registry.put(id, entry);
        nameToRegistry.put(location, entry);
        return id;
    }

    @Nullable
    public Entry get(ID<AnimationSet> id) {
        return registry.get(id);
    }

    @Nullable
    public Entry get(ResourceLocation name) {
        return nameToRegistry.get(name);
    }

    public void freeze() {
        freeze = true;
    }
}
