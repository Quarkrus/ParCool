package com.alrex.parcool.common.action;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.network.ActionStatePacket;
import com.alrex.parcool.common.network.ActionStateSetPacket;
import com.alrex.parcool.common.stamina.AbstractLocalStamina;
import com.alrex.parcool.common.stamina.StaminaSynchronizationDepot;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.PacketDistributor;
import org.apache.logging.log4j.Level;

import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

public class ActionProcessor {
	private final StaminaSynchronizationDepot staminaDepot = new StaminaSynchronizationDepot();
	private final ActionSynchronizationDepot actionDepot = new ActionSynchronizationDepot();

	public StaminaSynchronizationDepot getStaminaSyncDepot() {
		return staminaDepot;
	}

	public ActionSynchronizationDepot getActionSyncDepot() {
		return actionDepot;
	}

	@SubscribeEvent
	public void onTickPlayer(TickEvent.LevelTickEvent event) {
		if (event.phase == TickEvent.Phase.START) return;
		staminaDepot.tick();
		actionDepot.tick();
	}

	@SubscribeEvent
	public void onTickPlayer(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.START) return;

		var player = event.player;
		var parkourability = Parkourability.get(player);
		Map<String, LinkedList<ActionStatePacket.Entry>> synchronizedData = new TreeMap<>();
		if (event.side.isClient()) {
			onTick$doPreprocessInClient(parkourability);
		} else {
			onTick$doPreprocessInServer(parkourability);
		}

		if (player.isLocalPlayer()) {
			onTick$checkLimitationSynchronization(player, parkourability);
		}
		parkourability.getAdditionalProperties().onTick(player, parkourability);
		for (Action action : parkourability.getActions()) {
			MinecraftForge.EVENT_BUS.post(new ParCoolActionEvent.Tick.Pre(player, action));
			processAction(parkourability, event.side, action, synchronizedData);
			MinecraftForge.EVENT_BUS.post(new ParCoolActionEvent.Tick.Post(player, action));
		}
		if (!synchronizedData.isEmpty()) {
			onTick$sendSyncPacket(parkourability, event.side, synchronizedData);
		}
	}

	private void onTick$doPreprocessInServer(Parkourability parkourability) {
	}

	@OnlyIn(Dist.CLIENT)
	private void onTick$doPreprocessInClient(Parkourability parkourability) {
		if (parkourability.getStamina() instanceof AbstractLocalStamina stamina) {
			stamina.tick();
		}
	}

	private void onTick$sendSyncPacket(Parkourability parkourability, LogicalSide side, Map<String, LinkedList<ActionStatePacket.Entry>> synchronizedData) {
		var list = new LinkedList<ActionStatePacket>();
		for (var entry : synchronizedData.entrySet()) {
			if (entry.getValue().isEmpty()) continue;
			list.add(new ActionStatePacket(entry.getKey(), entry.getValue()));
		}
		if (list.isEmpty()) return;

		var packet = new ActionStateSetPacket(parkourability.player().getUUID());
		for (var subPacket : list) {
			packet.add(subPacket);
		}
		if (side.isClient()) {
			ParCool.CONNECTION.send(PacketDistributor.SERVER.noArg(), packet);
		} else {
			actionDepot.requestSync(packet);
		}
	}
    @OnlyIn(Dist.CLIENT)
	private void onTick$checkLimitationSynchronization(Player player, Parkourability parkourability) {
		if (player.isLocalPlayer() && player.tickCount > 127 && player.tickCount % 256 == 0 && parkourability.limitationIsNotSynced()) {
			if (player instanceof LocalPlayer localPlayer) {
				int trialCount = parkourability.getSynchronizeTrialCount();
				if (trialCount < 5) {
					parkourability.trySyncLimitation(localPlayer);
					if (ParCoolConfig.Client.SHOW_AUTO_RESYNCHRONIZATION_NOTIFICATION.get()) {
						player.displayClientMessage(Component.translatable("parcool.message.error.limitation.not_synced"), false);
					}
					ParCool.LOGGER.log(Level.WARN, "Detected ParCool Limitation is not synced. Sending synchronization request...");
				} else if (trialCount == 5) {
					parkourability.incrementSynchronizeTrialCount();
					player.displayClientMessage(Component.translatable("parcool.message.error.limitation.fail_sync").withStyle(ChatFormatting.DARK_RED), false);
					ParCool.LOGGER.log(Level.ERROR, "Failed to synchronize ParCool Limitation. There may be problems about server connection. Please report to the developer after checking connection");
				}
			}
		}
	}

	private void processAction(Parkourability parkourability, LogicalSide logicalSide, Action action, Map<String, LinkedList<ActionStatePacket.Entry>> synchronizedData) {
		var player = parkourability.player();
		var triggeredSide = action.getTriggeredSide();
		boolean needSync = (triggeredSide.isClient() && player.isLocalPlayer())
				|| (triggeredSide.isServer() && logicalSide.isServer());
		action.tick();
		action.onTick();
		if (logicalSide.isClient()) {
			action.onClientTick();
		} else {
			action.onServerTick();
		}
		ActionStatePacket.Type type = ActionStatePacket.Type.DATA;
		if (needSync) {
			if (action instanceof ContinuableAction continuableAction && continuableAction.isDoing()) {
				boolean canContinue = //TODO:parkourability.getActionInfo().can(action.getClass()) &&
						!MinecraftForge.EVENT_BUS.post(new ParCoolActionEvent.TryToContinue(parkourability.player(), continuableAction))
								&& continuableAction.canContinue();
				if (!canContinue) {
					MinecraftForge.EVENT_BUS.post(new ParCoolActionEvent.Finish.Pre(parkourability.player(), continuableAction));
					continuableAction.finish();
					MinecraftForge.EVENT_BUS.post(new ParCoolActionEvent.Finish.Post(parkourability.player(), continuableAction));
					type = ActionStatePacket.Type.FINISH;
				}
			} else {
				boolean start = !parkourability.player().isSpectator() //TODO
						&& parkourability.getActionInfo().can(action.getClass())
						&& !MinecraftForge.EVENT_BUS.post(new ParCoolActionEvent.TryToStart(parkourability.player(), action))
						&& action.canStart();
				if (start) {
					MinecraftForge.EVENT_BUS.post(new ParCoolActionEvent.Start.Pre(parkourability.player(), action));
					action.start();
					MinecraftForge.EVENT_BUS.post(new ParCoolActionEvent.Start.Post(parkourability.player(), action));
					type = ActionStatePacket.Type.START;
				}
			}
		}
		if (action instanceof ContinuableAction continuableAction && continuableAction.isDoing()) {
			continuableAction.onWorkingTick();
			if (logicalSide.isClient()) {
				continuableAction.onWorkingTickInClient();
				if (parkourability.player().isLocalPlayer()) {
					continuableAction.onWorkingTickInLocalClient();
				} else {
					continuableAction.onWorkingTickInOtherClient();
				}
			} else {
				continuableAction.onWorkingTickInServer();
			}
		}
		var data = action.getSynchronizedData().packToEntry(type, action.getEntry());
		if (data != null) {
			var list = synchronizedData.computeIfAbsent(data.entry().id().getNamespace(), __ -> new LinkedList<>());
			list.add(data);
		}
	}
}
