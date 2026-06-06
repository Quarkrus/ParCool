package com.alrex.parcool.client.animation.system.handle;

import com.alrex.parcool.client.animation.system.IPlayerAnimatorHolder;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TickEventHandler {
    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            var level = Minecraft.getInstance().level;
            if (level == null) return;
            for (var p : level.players()) {
                if (p instanceof IPlayerAnimatorHolder holder) {
                    holder.getParCoolPlayerAnimator().tick(p);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            var level = Minecraft.getInstance().level;
            if (level == null) return;
            for (var p : level.players()) {
                if (p instanceof IPlayerAnimatorHolder holder) {
                    holder.getParCoolPlayerAnimator().onRenderTick(p, event.renderTickTime);
                }
            }
        }
    }
}
