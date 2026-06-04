package com.alrex.parcool.client.animation;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.client.animation.system.data.AnimationSet;
import com.alrex.parcool.client.animation.system.registration.AnimationSets;
import com.alrex.parcool.client.animation.system.registration.ID;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ParCoolActions;
import net.minecraft.resources.ResourceLocation;

public class ParCoolAnimations {
    public static final ID<AnimationSet> FAST_RUN = AnimationSets.getInstance().register(
            new ResourceLocation(ParCool.MOD_ID, "fast_run"),
            () -> (p) -> Parkourability.get(p).get(ParCoolActions.FAST_RUN).isDoing(),
            null
    );

    public static void register() {
    }
}
