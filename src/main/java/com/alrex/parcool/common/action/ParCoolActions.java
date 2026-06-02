package com.alrex.parcool.common.action;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.event.RegisterParCoolActionEvent;
import com.alrex.parcool.common.action.impl.FastRun;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ParCoolActions {
    private static final ActionGroup GROUP;

    private static final ActionEntry<FastRun> FAST_RUN;
    //private static final ActionEntry<Crawl> CRAWL;
    //private static final ActionEntry<Slide> SLIDE;

    static {
        var BUILDER = new ActionGroup.Builder(ParCool.MOD_ID);
        FAST_RUN = BUILDER.add("fast_run", FastRun.class, FastRun::new, new StaminaConsumption((short) 0, (short) 1, (short) 0));
        //CRAWL = BUILDER.add("crawl", Crawl.class, Crawl::new, new StaminaConsumption());
        //SLIDE = BUILDER.add("slide", Slide.class, Slide::new, new StaminaConsumption(), CRAWL);
        GROUP = BUILDER.build();
    }

    @SubscribeEvent
    public static void onRegister(RegisterParCoolActionEvent event) {
        event.register(GROUP);
    }
}
