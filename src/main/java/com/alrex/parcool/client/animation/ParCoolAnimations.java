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
    public static final ID<AnimationSet> DODGE_RIGHT = AnimationSets.getInstance().register(
            new ResourceLocation(ParCool.MOD_ID, "dodge_right"),
            () -> (p) -> true,
            null
    );
    public static final ID<AnimationSet> DODGE_LEFT = AnimationSets.getInstance().register(
            new ResourceLocation(ParCool.MOD_ID, "dodge_left"),
            () -> (p) -> true,
            null
    );
    public static final ID<AnimationSet> DODGE_FRONT = AnimationSets.getInstance().register(
            new ResourceLocation(ParCool.MOD_ID, "dodge_front"),
            () -> (p) -> true,
            null
    );
    public static final ID<AnimationSet> DODGE_BACK = AnimationSets.getInstance().register(
            new ResourceLocation(ParCool.MOD_ID, "dodge_back"),
            () -> (p) -> true,
            null
    );
    public static final ID<AnimationSet> HANG_ON = AnimationSets.getInstance().register(
            new ResourceLocation(ParCool.MOD_ID, "hang_on"),
            () -> (p) -> Parkourability.get(p).get(ParCoolActions.HANG_ON).isDoing(),
            null
    );
    public static final ID<AnimationSet> CLIMB_UP = AnimationSets.getInstance().register(
            new ResourceLocation(ParCool.MOD_ID, "climb_up"),
            () -> (p) -> Parkourability.get(p).get(ParCoolActions.CLIMB_UP).isDoing(),
            null
    );
    public static final ID<AnimationSet> BACK_FLIP = AnimationSets.getInstance().register(
            new ResourceLocation(ParCool.MOD_ID, "back_flip"),
            () -> (p) -> !p.isOnGround(),
            null
    );
    public static final ID<AnimationSet> SLIDE_DOWN = AnimationSets.getInstance().register(
            new ResourceLocation(ParCool.MOD_ID, "slide_down"),
            () -> (p) -> Parkourability.get(p).get(ParCoolActions.SLIDE_DOWN).isDoing(),
            null
    );

    public static void register() {
    }
}
