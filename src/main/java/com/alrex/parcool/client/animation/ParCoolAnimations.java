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
    public static final ID<AnimationSet> CRAWL = AnimationSets.getInstance().register(
            new ResourceLocation(ParCool.MOD_ID, "crawl"),
            () -> (p) -> Parkourability.get(p).get(ParCoolActions.CRAWL).isDoing(),
            null
    );
    public static final ID<AnimationSet> DODGE_SIDE = AnimationSets.getInstance().register(
            new ResourceLocation(ParCool.MOD_ID, "side_dodge"),
            () -> (p) -> true,
            null
    );
    public static final ID<AnimationSet> HANG_ON = AnimationSets.getInstance().register(
            new ResourceLocation(ParCool.MOD_ID, "hang_on"),
            () -> (p) -> Parkourability.get(p).get(ParCoolActions.HANG_ON).isDoing(),
            null
    );

    public static void register() {
    }
}
