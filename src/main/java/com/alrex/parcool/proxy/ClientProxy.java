package com.alrex.parcool.proxy;

import com.alrex.parcool.client.animation.ParCoolAnimationProgresses;
import com.alrex.parcool.client.animation.ParCoolAnimations;
import com.alrex.parcool.client.animation.ParCoolBlendingFactors;
import com.alrex.parcool.client.animation.ParCoolCodedAnimationComponents;
import com.alrex.parcool.client.animation.system.handle.TickEventHandler;
import com.alrex.parcool.client.hud.HUDRegistry;
import com.alrex.parcool.client.input.ParCoolKeyBinds;
import com.alrex.parcool.common.handlers.InputHandler;
import com.alrex.parcool.common.handlers.OpenSettingsParCoolHandler;
import com.alrex.parcool.common.network.ActionStateSetPacket;
import com.alrex.parcool.common.network.MultiActionStateSetPacket;
import com.alrex.parcool.common.network.MultiStaminaPacket;
import com.alrex.parcool.common.network.StaminaPacket;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.simple.SimpleChannel;

@OnlyIn(Dist.CLIENT)
public class ClientProxy extends CommonProxy {

	@Override
	public void init() {
		super.init();
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(ParCoolKeyBinds::registerAll);
		bus.addListener(HUDRegistry.getInstance()::onSetup);
		MinecraftForge.EVENT_BUS.addListener(ParCoolKeyBinds::tick);
		MinecraftForge.EVENT_BUS.register(HUDRegistry.getInstance());
		MinecraftForge.EVENT_BUS.register(OpenSettingsParCoolHandler.class);
		MinecraftForge.EVENT_BUS.register(InputHandler.class);
        MinecraftForge.EVENT_BUS.register(TickEventHandler.class);
		ParCoolAnimationProgresses.register();
        ParCoolCodedAnimationComponents.register();
        ParCoolBlendingFactors.register();
		ParCoolAnimations.register();
	}

	@Override
	public void registerMessages(SimpleChannel instance) {
		int index = 0;
		instance.messageBuilder(StaminaPacket.class, index++)
				.noResponse()
				.decoder(StaminaPacket.HANDLER::decode)
				.encoder(StaminaPacket.HANDLER::encode)
				.consumerMainThread(StaminaPacket.HANDLER::handleInPhysicalClient)
				.add();
		instance.messageBuilder(MultiStaminaPacket.class, index++)
				.noResponse()
				.decoder((packet) -> MultiStaminaPacket.decode(MultiStaminaPacket::new, packet))
				.encoder(MultiStaminaPacket::encode)
				.consumerMainThread(MultiStaminaPacket::handleInPhysicalClient)
				.add();
		instance.messageBuilder(ActionStateSetPacket.class, index++)
				.noResponse()
				.decoder(ActionStateSetPacket.HANDLER::decode)
				.encoder(ActionStateSetPacket.HANDLER::encode)
				.consumerMainThread(ActionStateSetPacket.HANDLER::handleInPhysicalClient)
				.add();
		instance.messageBuilder(MultiActionStateSetPacket.class, index++)
				.noResponse()
				.decoder((packet) -> MultiActionStateSetPacket.decode(MultiActionStateSetPacket::new, packet))
				.encoder(MultiActionStateSetPacket::encode)
				.consumerMainThread(MultiActionStateSetPacket::handleInPhysicalClient)
				.add();
	}
}
