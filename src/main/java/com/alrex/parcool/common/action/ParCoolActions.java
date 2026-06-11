package com.alrex.parcool.common.action;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.event.RegisterParCoolActionEvent;
import com.alrex.parcool.common.action.impl.ClingToCliff;
import com.alrex.parcool.common.action.impl.Crawl;
import com.alrex.parcool.common.action.impl.Dodge;
import com.alrex.parcool.common.action.impl.FastRun;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ParCoolActions {
    private static final ActionGroup GROUP;

    public static final ActionEntry<FastRun> FAST_RUN;
    public static final ActionEntry<Crawl> CRAWL;
    public static final ActionEntry<ClingToCliff> CLING_TO_CLIFF;
    private static final ActionEntry<Dodge> DODGE;

    static {
        var builder = new ActionGroup.Builder(ParCool.MOD_ID);
        FAST_RUN = builder.add("fast_run", FastRun.class, FastRun::new, StaminaConsumption.get(0, 1, 0));
        CRAWL = builder.add("crawl", Crawl.class, Crawl::new, StaminaConsumption.get(0, 0, 0));
        CLING_TO_CLIFF = builder.add("cling_to_cliff", ClingToCliff.class, ClingToCliff::new, StaminaConsumption.get(0, 0, 0));
        DODGE = builder.add("dodge", Dodge.class, Dodge::new, StaminaConsumption.get(0, 0, 0));
        GROUP = builder.build();
    }

    @SubscribeEvent
    public static void onRegister(RegisterParCoolActionEvent event) {
        event.register(GROUP);
    }
}
