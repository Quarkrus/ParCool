package com.alrex.parcool.common.handlers;

import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayerDamageHandler {
    @SubscribeEvent
    public static void onAttack(LivingAttackEvent event) {
    }

    @SubscribeEvent
    public static void onFall(LivingFallEvent event) {
	}
}
