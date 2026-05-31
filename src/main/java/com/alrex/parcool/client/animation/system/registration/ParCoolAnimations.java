package com.alrex.parcool.client.animation.system.registration;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.client.animation.system.data.AnimationSet;
import net.minecraft.resources.ResourceLocation;

public class ParCoolAnimations {
    public static final ID<AnimationSet> TEST = AnimationSets.getInstance().register(
            new ResourceLocation(ParCool.MOD_ID, "test"),
            () -> (p) -> true,
            null
    );

    public static void register() {
    }
}
