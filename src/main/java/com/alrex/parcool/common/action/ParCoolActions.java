package com.alrex.parcool.common.action;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.event.RegisterParCoolActionEvent;
import com.alrex.parcool.common.action.impl.Crawl;
import com.alrex.parcool.common.action.impl.FastRun;
import com.alrex.parcool.common.action.impl.Slide;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ParCoolActions {
    private static final ActionGroup GROUP;

    public static final ActionEntry<FastRun> FAST_RUN;
    public static final ActionEntry<Crawl> CRAWL;
    private static final ActionEntry<Slide> SLIDE;

    static {
        var BUILDER = new ActionGroup.Builder(ParCool.MOD_ID);
        FAST_RUN = BUILDER.add("fast_run", FastRun.class, FastRun::new, StaminaConsumption.get(0, 1, 0));
        CRAWL = BUILDER.add("crawl", Crawl.class, Crawl::new, StaminaConsumption.get(0, 0, 0));
        SLIDE = BUILDER.add("slide", Slide.class, Slide::new, StaminaConsumption.get(0, 0, 0), CRAWL);
        GROUP = BUILDER.build();
    }

    @SubscribeEvent
    public static void onRegister(RegisterParCoolActionEvent event) {
        event.register(GROUP);
    }
}
