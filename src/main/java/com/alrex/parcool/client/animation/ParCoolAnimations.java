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
    public static final ID<AnimationSet> TAP = AnimationSets.getInstance().register(
            new ResourceLocation(ParCool.MOD_ID, "tap"),
            () -> (p) -> true,
            null
    );
    public static final ID<AnimationSet> ROLL = AnimationSets.getInstance().register(
            new ResourceLocation(ParCool.MOD_ID, "roll"),
            () -> (p) -> true,
            null
    );
    public static final ID<AnimationSet> HORIZONTAL_WALL_RUN = AnimationSets.getInstance().register(
            new ResourceLocation(ParCool.MOD_ID, "horizontal_wall_run"),
            () -> (p) -> Parkourability.get(p).get(ParCoolActions.HORIZONTAL_WALL_RUN).isDoing(),
            null
    );
    public static final ID<AnimationSet> SLIDE = AnimationSets.getInstance().register(
            new ResourceLocation(ParCool.MOD_ID, "slide"),
            () -> (p) -> Parkourability.get(p).get(ParCoolActions.SLIDE).isDoing(),
            CRAWL
    );
    public static final ID<AnimationSet> DIVE = AnimationSets.getInstance().register(
            new ResourceLocation(ParCool.MOD_ID, "dive"),
            () -> (p) -> Parkourability.get(p).get(ParCoolActions.DIVE).isDoing(),
            null
    );
    public static final ID<AnimationSet> SKYDIVE = AnimationSets.getInstance().register(
            new ResourceLocation(ParCool.MOD_ID, "skydive"),
            () -> (p) -> Parkourability.get(p).get(ParCoolActions.SKYDIVE).isDoing(),
            DIVE
    );

    public static void register() {
    }
}
