package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.stamina.ReadonlyStamina;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

public record StaminaPacket(UUID playerID, ReadonlyStamina stamina) {
	public static final IHandler<StaminaPacket> HANDLER = new IHandler<StaminaPacket>() {
		@Override
		public void encode(StaminaPacket staminaPacket, FriendlyByteBuf packet) {
			packet.writeLong(staminaPacket.playerID.getMostSignificantBits());
			packet.writeLong(staminaPacket.playerID.getLeastSignificantBits());
			packet.writeInt(staminaPacket.stamina.value());
			packet.writeInt(staminaPacket.stamina.max());
			packet.writeBoolean(staminaPacket.stamina.isExhausted());
			packet.writeBoolean(staminaPacket.stamina.imposePenalty());
		}

		@Override
		public StaminaPacket decode(FriendlyByteBuf packet) {
			return new StaminaPacket(
					new UUID(packet.readLong(), packet.readLong()),
					new ReadonlyStamina(packet.readInt(), packet.readInt(), packet.readBoolean(), packet.readBoolean())
			);
		}

		@Override
		public void handleInPhysicalServer(StaminaPacket staminaPacket, Supplier<NetworkEvent.Context> contextSupplier) {
			ServerPlayer player;
			player = contextSupplier.get().getSender();
			ParCool.CONNECTION.send(PacketDistributor.ALL.noArg(), this);
			if (player == null) return;
			var parkourability = Parkourability.get(player);
			parkourability.updateStaminaInRemote(staminaPacket.stamina);

			ParCool.getActionProcessor().getStaminaSyncDepot().requestSync(player.getUUID(), staminaPacket.stamina);
		}

		@Override
		public void handleInPhysicalClient(StaminaPacket staminaPacket, Supplier<NetworkEvent.Context> contextSupplier) {
			Player player;
			boolean isInLogicalServer = contextSupplier.get().getDirection().getReceptionSide() == LogicalSide.SERVER;
			if (isInLogicalServer) {
				player = contextSupplier.get().getSender();
				if (player == null) return;
			} else {
				var world = Minecraft.getInstance().level;
				if (world == null) return;
				player = world.getPlayerByUUID(staminaPacket.playerID);
				if (player == null || player.isLocalPlayer()) return;
			}
			var parkourability = Parkourability.get(player);
			parkourability.updateStaminaInRemote(staminaPacket.stamina);

			if (isInLogicalServer) {
				ParCool.getActionProcessor().getStaminaSyncDepot().requestSync(player.getUUID(), staminaPacket.stamina);
			}
		}
	};
}
