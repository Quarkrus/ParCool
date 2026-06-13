package com.alrex.parcool.common.action;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.event.RegisterParCoolActionEvent;
import com.alrex.parcool.common.action.impl.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ParCoolActions {
    private static final ActionGroup GROUP;

    public static final ActionEntry<FastRun> FAST_RUN;
    public static final ActionEntry<Crawl> CRAWL;
    public static final ActionEntry<HangOn> HANG_ON;
    public static final ActionEntry<Dodge> DODGE;
    public static final ActionEntry<ClimbUp> CLIMB_UP;

    static {
        var builder = new ActionGroup.Builder(ParCool.MOD_ID);
        FAST_RUN = builder.add("fast_run", FastRun.class, FastRun::new, new ActionOption().cost(StaminaConsumption.get(0, 1, 0)));
        CRAWL = builder.add("crawl", Crawl.class, Crawl::new, new ActionOption().needPose(null));
        HANG_ON = builder.add("hang_on", HangOn.class, HangOn::new, new ActionOption().cost(StaminaConsumption.get(0, 1, 0)));
        CLIMB_UP = builder.add("climb_up", ClimbUp.class, ClimbUp::new, new ActionOption().parent(HANG_ON).needParentWorking(false));
        DODGE = builder.add("dodge", Dodge.class, Dodge::new);
        GROUP = builder.build();
    }

    @SubscribeEvent
    public static void onRegister(RegisterParCoolActionEvent event) {
        event.register(GROUP);
    }
}
