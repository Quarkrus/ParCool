package com.alrex.parcool.proxy;

import com.alrex.parcool.common.network.ActionStateSetPacket;
import com.alrex.parcool.common.network.ClientBoundParCoolLoginPacket;
import com.alrex.parcool.common.network.MultiStaminaPacket;
import com.alrex.parcool.common.network.StaminaPacket;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.simple.SimpleChannel;

@OnlyIn(Dist.DEDICATED_SERVER)
public class ServerProxy extends CommonProxy {
	@Override
	public void registerMessages(SimpleChannel instance) {
		int index = 0;
		instance.messageBuilder(ClientBoundParCoolLoginPacket.class, index++, NetworkDirection.LOGIN_TO_CLIENT)
				.markAsLoginPacket()
				.encoder(ClientBoundParCoolLoginPacket::encode)
				.decoder(ClientBoundParCoolLoginPacket::decode)
				.add();
		instance.messageBuilder(StaminaPacket.class, index++)
				.noResponse()
				.decoder(StaminaPacket.HANDLER::decode)
				.encoder(StaminaPacket.HANDLER::encode)
				.consumerMainThread(StaminaPacket.HANDLER::handleInPhysicalServer)
				.add();
		instance.messageBuilder(MultiStaminaPacket.class, index++)
				.noResponse()
				.decoder((packet) -> MultiStaminaPacket.decode(MultiStaminaPacket::new, packet))
				.encoder(MultiStaminaPacket::encode)
				.consumerMainThread(MultiStaminaPacket::handleInPhysicalServer)
				.add();
		instance.messageBuilder(ActionStateSetPacket.class, index++)
				.noResponse()
				.decoder(ActionStateSetPacket::decode)
				.encoder(ActionStateSetPacket::encode)
				.consumerMainThread(ActionStateSetPacket::handleInPhysicalServer)
				.add();
	}
}
