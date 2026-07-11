package com.alrex.parcool.common.action;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.action.*;
import com.alrex.parcool.common.action.impl.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

public class ParCoolActions {
    private static final ActionGroup GROUP;

    public static final ActionEntry<FastRun> FAST_RUN;
    public static final ActionEntry<Vault> VAULT;
    public static final ActionEntry<Dive> DIVE;
    public static final ActionEntry<Skydive> SKYDIVE;
    public static final ActionEntry<Crawl> CRAWL;
    public static final ActionEntry<Slide> SLIDE;
    public static final ActionEntry<HangOn> HANG_ON;
    public static final ActionEntry<HangDown> HANG_DOWN;
    public static final ActionEntry<Dodge> DODGE;
    public static final ActionEntry<ClimbUp> CLIMB_UP;
    public static final ActionEntry<TrickJump> TRICK_JUMP;
    public static final ActionEntry<SlideDown> SLIDE_DOWN;
    public static final ActionEntry<Breakfall> BREAKFALL;
    public static final ActionEntry<HorizontalWallRun> HORIZONTAL_WALL_RUN;
    public static final ActionEntry<ChargeJump> CHARGE_JUMP;
    public static final ActionEntry<WallJump> WALL_JUMP;
    public static final ActionEntry<HideInBlock> HIDE_IN_BLOCK;

    static {
        var builder = new ActionGroup.Builder(ParCool.MOD_ID);
        WALL_JUMP = builder.add("wall_jump", WallJump.class, WallJump::new, new ActionOption()
                .cost(StaminaConsumption.get(50, 0, 0))
                .needNotOnGround(true)
        );

        FAST_RUN = builder.add("fast_run", FastRun.class, FastRun::new, new ActionOption()
                .cost(StaminaConsumption.get(0, 2, 0))
        );
        {
            VAULT = builder.add("vault", Vault.class, Vault::new, new ActionOption()
                    .parent(FAST_RUN).needParentWorking(true)
                    .cost(StaminaConsumption.get(50, 0, 0))
            );
            HORIZONTAL_WALL_RUN = builder.add("horizontal_wall_run", HorizontalWallRun.class, HorizontalWallRun::new, new ActionOption()
                    .parent(FAST_RUN).needParentWorking(true)
                    .needNotOnGround(true)
                    .cost(StaminaConsumption.get(0, 3, 0))
            );
            DIVE = builder.add("dive", Dive.class, Dive::new, new ActionOption()
                    .parent(FAST_RUN).needParentWorking(false)
                    .needNotOnGround(true)
            );
            {
                SKYDIVE = builder.add("skydive", Skydive.class, Skydive::new, new ActionOption()
                        .parent(DIVE).needParentWorking(true)
                );
            }
        }

        HIDE_IN_BLOCK = builder.add("hide_in_block", HideInBlock.class, HideInBlock::new, new ActionOption()
                .needPose(null)
        );
        {
            CRAWL = builder.add("crawl", Crawl.class, Crawl::new, new ActionOption()
                    .parent(HIDE_IN_BLOCK).needParentWorking(false)
                    .needPose(null)
            );
            {
                SLIDE = builder.add("slide", Slide.class, Slide::new, new ActionOption()
                        .parent(CRAWL).needParentWorking(true)
                        .needPose(null)
                );
            }
        }

        HANG_ON = builder.add("hang_on", HangOn.class, HangOn::new, new ActionOption()
                .cost(StaminaConsumption.get(0, 3, 0))
        );
        {
            CLIMB_UP = builder.add("climb_up", ClimbUp.class, ClimbUp::new, new ActionOption()
                    .parent(HANG_ON).needParentWorking(false)
                    .cost(StaminaConsumption.get(50, 0, 0))
            );
            HANG_DOWN = builder.add("hang_down", HangDown.class, HangDown::new, new ActionOption()
                    .parent(HANG_ON).needParentWorking(false)
                    .needNotOnGround(true)
            );
            SLIDE_DOWN = builder.add("slide_down", SlideDown.class, SlideDown::new, new ActionOption()
                    .parent(HANG_ON).needParentWorking(false)
                    .needNotOnGround(true)
                    .cost(StaminaConsumption.get(0, 1, 0))
            );
        }

        DODGE = builder.add("dodge", Dodge.class, Dodge::new, new ActionOption()
                .needOnGround(true)
                .cost(StaminaConsumption.get(50, 0, 0))
        );
        TRICK_JUMP = builder.add("trick_jump", TrickJump.class, TrickJump::new);
        BREAKFALL = builder.add("breakfall", Breakfall.class, Breakfall::new, new ActionOption()
                .triggeredSide(LogicalSide.SERVER)
                .cost(StaminaConsumption.get(50, 0, 0))
        );
        CHARGE_JUMP = builder.add("charge_jump", ChargeJump.class, ChargeJump::new, new ActionOption()
                .cost(StaminaConsumption.get(0, 0, 50))
                .needPose(null)
        );
        GROUP = builder.build();
    }

    @SubscribeEvent
    public static void onRegister(RegisterParCoolActionEvent event) {
        event.register(GROUP);
    }
}
