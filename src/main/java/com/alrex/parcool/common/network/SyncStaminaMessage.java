package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.stamina.OtherStamina;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncStaminaMessage {

	private int stamina = 0;
    private int max = 0;
	private boolean exhausted = false;
	private boolean imposingPenalty = false;
	private int consumeOnServer = 0;
	private int staminaType = -1;
	private UUID playerID = null;

	public void encode(FriendlyByteBuf packet) {
		packet.writeInt(this.stamina);
        packet.writeInt(this.max);
		packet.writeBoolean(this.exhausted);
		packet.writeBoolean(this.imposingPenalty);
		packet.writeInt(this.staminaType);
		packet.writeInt(this.consumeOnServer);
		packet.writeLong(this.playerID.getMostSignificantBits());
		packet.writeLong(this.playerID.getLeastSignificantBits());
	}

	public static SyncStaminaMessage decode(FriendlyByteBuf packet) {
		SyncStaminaMessage message = new SyncStaminaMessage();
		message.stamina = packet.readInt();
        message.max = packet.readInt();
		message.exhausted = packet.readBoolean();
		message.imposingPenalty = packet.readBoolean();
		message.staminaType = packet.readInt();
		message.consumeOnServer = packet.readInt();
		message.playerID = new UUID(packet.readLong(), packet.readLong());
		return message;
	}

	@OnlyIn(Dist.DEDICATED_SERVER)
	public void handleServer(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			ServerPlayer player;
			player = contextSupplier.get().getSender();
			ParCool.CHANNEL_INSTANCE.send(PacketDistributor.ALL.noArg(), this);
			if (player == null) return;
			IStamina stamina = IStamina.get(player);
			if (!(stamina instanceof OtherStamina)) return;
			OtherStamina otherStamina = (OtherStamina) stamina;
			otherStamina.setMax(this.max);
			otherStamina.set(this.stamina);
			otherStamina.setImposingPenalty(imposingPenalty);
			otherStamina.setExhaustion(exhausted);
			if (staminaType != -1 && consumeOnServer > 0) {
				IStamina.Type.values()[staminaType].handleConsumeOnServer(player, consumeOnServer);
			}

			SyncStaminaToClientMessage.requestSync(player.getUUID(), otherStamina);
		});
		contextSupplier.get().setPacketHandled(true);
	}

	@OnlyIn(Dist.CLIENT)
	public void handleClient(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			ServerPlayer serverPlayer = null;
			Player player;
			boolean isInLogicalServer = contextSupplier.get().getDirection().getReceptionSide() == LogicalSide.SERVER;
			if (isInLogicalServer) {
				player = serverPlayer = contextSupplier.get().getSender();
				if (player == null) return;
			} else {
				Level world = Minecraft.getInstance().level;
				if (world == null) return;
				player = world.getPlayerByUUID(playerID);
				if (player == null || player.isLocalPlayer()) return;
			}
			IStamina stamina = IStamina.get(player);
			if (!(stamina instanceof OtherStamina)) return;
			OtherStamina otherStamina = (OtherStamina) stamina;
			otherStamina.setMax(this.max);
			otherStamina.set(this.stamina);
			otherStamina.setImposingPenalty(imposingPenalty);
			otherStamina.setExhaustion(exhausted);
			if (serverPlayer != null && staminaType != -1 && consumeOnServer > 0) {
				IStamina.Type.values()[staminaType].handleConsumeOnServer(serverPlayer, consumeOnServer);
			}
			if (isInLogicalServer) {
				SyncStaminaToClientMessage.requestSync(player.getUUID(), otherStamina);
			}
		});
		contextSupplier.get().setPacketHandled(true);
	}

	@OnlyIn(Dist.CLIENT)
	public static void sync(Player player) {
		IStamina stamina = IStamina.get(player);
		if (stamina == null || !player.isLocalPlayer()) return;

		SyncStaminaMessage message = new SyncStaminaMessage();
		message.stamina = stamina.get();
        message.max = stamina.getActualMaxStamina();
		message.exhausted = stamina.isExhausted();
		message.imposingPenalty = stamina.isImposingExhaustionPenalty();
		message.consumeOnServer = stamina.getRequestedValueConsumedOnServer();
		IStamina.Type type = IStamina.Type.getFromInstance(stamina);
		message.staminaType = type != null ? type.ordinal() : -1;
		message.playerID = player.getUUID();

		ParCool.CHANNEL_INSTANCE.sendToServer(message);
	}
}
