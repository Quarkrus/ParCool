package com.alrex.parcool;

import com.alrex.parcool.api.Attributes;
import com.alrex.parcool.api.Effects;
import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.api.event.RegisterParCoolActionEvent;
import com.alrex.parcool.client.renderer.Renderers;
import com.alrex.parcool.common.action.ActionRegistry;
import com.alrex.parcool.common.action.ParCoolActions;
import com.alrex.parcool.common.block.Blocks;
import com.alrex.parcool.common.block.TileEntities;
import com.alrex.parcool.common.entity.EntityTypes;
import com.alrex.parcool.common.handlers.AddAttributesHandler;
import com.alrex.parcool.common.item.Items;
import com.alrex.parcool.common.item.recipe.Recipes;
import com.alrex.parcool.common.potion.PotionRecipeRegistry;
import com.alrex.parcool.common.potion.Potions;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.extern.AdditionalMods;
import com.alrex.parcool.proxy.ClientProxy;
import com.alrex.parcool.proxy.CommonProxy;
import com.alrex.parcool.proxy.ServerProxy;
import com.alrex.parcool.server.command.CommandRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ParCool.MOD_ID)
public class ParCool {
	public static final String MOD_ID = "parcool";
	private static final String PROTOCOL_VERSION = "4.0.0.0";
	public static final SimpleChannel CHANNEL_INSTANCE = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(ParCool.MOD_ID, "message"),
			() -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals
	);
	public static final CommonProxy PROXY = DistExecutor.unsafeRunForDist(
			() -> ClientProxy::new,
			() -> ServerProxy::new
	);
	public static final Logger LOGGER = LogManager.getLogger();

	public static boolean isActive() {
		return PROXY.ParCoolIsActive();
	}

	private final ActionRegistry actionRegistry = new ActionRegistry();

	public ParCool() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		eventBus.addListener(this::setup);
		eventBus.addListener(this::setupClient);
		eventBus.addListener(this::loaded);
		eventBus.register(AddAttributesHandler.class);
		eventBus.register(ParCoolActions.class);

		PROXY.init();

		Effects.register(eventBus);
		Potions.register(eventBus);
		Attributes.register(eventBus);
		SoundEvents.register(eventBus);
		Blocks.register(eventBus);
		Items.register(eventBus);
		Recipes.register(eventBus);
		EntityTypes.register(eventBus);
		TileEntities.register(eventBus);

		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ParCoolConfig.Client.BUILT_CONFIG);
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ParCoolConfig.CLIENT_CONFIG_LIMITATION.getBuiltConfig(), "parcool-client-limitation.toml");
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ParCoolConfig.SERVER_CONFIG_LIMITATION.getBuiltConfig(), "parcool-server-limitation.toml");
	}

	private void loaded(FMLLoadCompleteEvent event) {
		PotionRecipeRegistry.register();
		AdditionalMods.init();
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> AdditionalMods::initInClient);
		DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER, () -> AdditionalMods::initInDedicatedServer);

		FMLJavaModLoadingContext.get().getModEventBus().post(new RegisterParCoolActionEvent(actionRegistry));
		actionRegistry.freeze();
	}

	private void setup(final FMLCommonSetupEvent event) {
		CommandRegistry.registerArgumentTypes(event);
		PROXY.registerMessages(CHANNEL_INSTANCE);
	}

	private void setupClient(final FMLClientSetupEvent event) {
		Renderers.register();
		Items.registerColors();
	}
}
