package com.alrex.parcool.client.animation.system.resource;

import com.alrex.parcool.client.animation.system.AnimationComponentGroup;
import com.alrex.parcool.client.animation.system.AnimationComponent;
import com.alrex.parcool.client.animation.system.AnimationSet;
import com.alrex.parcool.client.animation.system.registration.AnimationSets;
import com.alrex.parcool.client.animation.system.registration.ID;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class AnimationResource {
    private final Map<ResourceLocation, AnimationComponent> componentMap;
    private final Map<ResourceLocation, AnimationComponentGroup> animationGroupMap;
    private final Map<ResourceLocation, AnimationSet> animationSetMap;
    private final Map<ID<AnimationSet>, AnimationSet> idAnimationSetMap;

    public static AnimationResource empty() {
        return new AnimationResource(Collections.EMPTY_MAP, Collections.EMPTY_MAP, Collections.EMPTY_MAP);
    }

    public AnimationResource(
            Map<ResourceLocation, AnimationComponent> componentMap,
            Map<ResourceLocation, AnimationComponentGroup> animationGroupMap,
            Map<ResourceLocation, AnimationSet> animationSetMap
    ) {
        this.componentMap = componentMap;
        this.animationGroupMap = animationGroupMap;
        this.animationSetMap = animationSetMap;
        this.idAnimationSetMap = new TreeMap<>();
        for (var mapEntry : this.animationSetMap.entrySet()) {
            var entry = AnimationSets.getInstance().get(mapEntry.getKey());
            if (entry != null) {
                this.idAnimationSetMap.put(entry.id(), mapEntry.getValue());
            }
        }
    }

    @Nullable
    public AnimationSet getAnimationSet(ID<AnimationSet> id) {
        return idAnimationSetMap.get(id);
    }

    @Nullable
    public AnimationSet getAnimationSet(ResourceLocation name) {
        return animationSetMap.get(name);
    }

}
