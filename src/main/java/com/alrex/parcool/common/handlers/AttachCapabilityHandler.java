package com.alrex.parcool.common.handlers;

import com.alrex.parcool.common.capability.capabilities.Capabilities;
import com.alrex.parcool.common.capability.provider.StaminaProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class AttachCapabilityHandler {

	@SubscribeEvent
    public static void onAttachCapability(AttachCapabilitiesEvent<net.minecraft.world.entity.Entity> event) {
        if (!(event.getObject() instanceof Player player)) return;
        event.addCapability(Capabilities.STAMINA_LOCATION, new StaminaProvider(player));
	}
}
