package com.alrex.parcool.common.handlers;

import com.alrex.parcool.common.Parkourability;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayerJumpHandler {
	@SubscribeEvent
	public static void onJump(LivingEvent.LivingJumpEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;
		parkourability.getAdditionalProperties().onJump();
	}
}
