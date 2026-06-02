package com.alrex.parcool.common.handlers;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.info.CompiledLimitation;
import com.alrex.parcool.common.network.LimitationPacket;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.server.limitation.Limitation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;

public class LoginLogoutHandler {
	@SubscribeEvent
	public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
		var player = event.getEntity();
		if (player instanceof ServerPlayer) {
            ParCool.getLimitationRegistry().unload(player.getUUID());
		}
	}
	@SubscribeEvent
	public static void onLogin(ClientPlayerNetworkEvent.LoggingIn event) {
		var player = event.getPlayer();
		ParCool.CONNECTION.send(
				PacketDistributor.SERVER.noArg(),
				new LimitationPacket(player.getUUID(), false, true,
						CompiledLimitation.compile(Limitation.readFromConfig(
								ParCoolConfig.getClientConfigLimitation(),
								ParCool.getActionRegistry(),
								ParCool.getStaminaTypeRegistry())
						)
				)
		);
	}
}
