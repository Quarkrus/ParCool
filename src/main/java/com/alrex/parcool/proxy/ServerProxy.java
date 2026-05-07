package com.alrex.parcool.proxy;

import com.alrex.parcool.common.network.ClientBoundParCoolLoginPacket;
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
				.decoder(ClientBoundParCoolLoginPacket::decode);
	}
}
