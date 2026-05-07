package com.alrex.parcool.proxy;

import com.alrex.parcool.client.hud.HUDRegistry;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.handlers.EnableOrDisableParCoolHandler;
import com.alrex.parcool.common.handlers.InputHandler;
import com.alrex.parcool.common.handlers.OpenSettingsParCoolHandler;
import com.alrex.parcool.common.handlers.PlayerJoinHandler;
import com.alrex.parcool.common.network.ClientBoundParCoolLoginPacket;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.simple.SimpleChannel;

@OnlyIn(Dist.CLIENT)
public class ClientProxy extends CommonProxy {
	@Override
	public boolean ParCoolIsActive() {
		return ParCoolConfig.Client.Booleans.ParCoolIsActive.get();
	}

	@Override
	public void init() {
		super.init();
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(KeyBindings::register);
		bus.addListener(HUDRegistry.getInstance()::onSetup);
		MinecraftForge.EVENT_BUS.register(HUDRegistry.getInstance());
		MinecraftForge.EVENT_BUS.register(KeyRecorder.class);
		MinecraftForge.EVENT_BUS.register(OpenSettingsParCoolHandler.class);
		MinecraftForge.EVENT_BUS.register(EnableOrDisableParCoolHandler.class);
		MinecraftForge.EVENT_BUS.register(PlayerJoinHandler.class);
		MinecraftForge.EVENT_BUS.register(InputHandler.class);
	}

	@Override
	public void registerMessages(SimpleChannel instance) {
		int index = 0;
		instance.messageBuilder(ClientBoundParCoolLoginPacket.class, index++, NetworkDirection.LOGIN_TO_CLIENT)
				.markAsLoginPacket()
				.encoder(ClientBoundParCoolLoginPacket::encode)
				.decoder(ClientBoundParCoolLoginPacket::decode)
				.consumerMainThread(ClientBoundParCoolLoginPacket::handle);

	}
}
