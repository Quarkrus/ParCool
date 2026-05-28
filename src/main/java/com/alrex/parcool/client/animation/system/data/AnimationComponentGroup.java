package com.alrex.parcool.client.animation.system.data;

import com.alrex.parcool.client.animation.system.IAnimationProgress;
import com.alrex.parcool.client.animation.system.IBlendingFactor;
import com.alrex.parcool.client.animation.system.registration.ID;

import javax.annotation.Nullable;
import java.util.List;

public record AnimationComponentGroup(List<ComponentEntry> components, int duration, boolean loops) {
    public record ComponentEntry(IAnimationComponent component, @Nullable IBlendingFactor blendingFactor,
                                 ID<IAnimationProgress> progressID) {
    }

}
