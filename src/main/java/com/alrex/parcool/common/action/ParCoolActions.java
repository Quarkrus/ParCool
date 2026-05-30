package com.alrex.parcool.common.action;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.event.RegisterParCoolActionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ParCoolActions {
    private static final ActionGroup.Builder BUILDER = new ActionGroup.Builder(ParCool.MOD_ID);

    private static final ActionEntry<Crawl> CRAWL;
    private static final ActionEntry<Slide> SLIDE;

    static {
        CRAWL = BUILDER.add("crawl", Crawl.class, Crawl::new, new StaminaConsumption());
        SLIDE = BUILDER.add("slide", Slide.class, Slide::new, new StaminaConsumption(), CRAWL);
    }

    @SubscribeEvent
    public static void onRegister(RegisterParCoolActionEvent event) {
        event.register(BUILDER.build());
    }
}
