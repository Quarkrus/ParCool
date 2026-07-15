package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.info.CompiledLimitation;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.server.limitation.Limitation;
import com.alrex.parcool.util.NetworkUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import org.apache.logging.log4j.Level;

import java.util.UUID;
import java.util.function.Supplier;

public record LimitationPacket(UUID playerID, boolean serverLimitation, boolean requestReply,
                               CompiledLimitation limitation) {
    public LimitationPacket noResponse() {
        return new LimitationPacket(playerID, serverLimitation, false, limitation);
    }

    public boolean clientLimitation() {
        return !serverLimitation;
    }

    public void encode(FriendlyByteBuf packet) {
        packet.writeUUID(playerID);
        packet.writeBoolean(serverLimitation);
        packet.writeBoolean(requestReply);
        limitation.writeTo(packet);
    }

    public static LimitationPacket decode(FriendlyByteBuf packet) {
        return new LimitationPacket(packet.readUUID(), packet.readBoolean(), packet.readBoolean(), CompiledLimitation.readFrom(packet));
    }

    @OnlyIn(Dist.CLIENT)
    public void handleInPhysicalClient(Supplier<NetworkEvent.Context> contextSupplier) {
        var context = contextSupplier.get();
        var player = NetworkUtil.getPlayerInPhysicalClient(playerID, context, clientLimitation());
        if (player == null) return;
        handle(player, context);
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    public void handleInPhysicalServer(Supplier<NetworkEvent.Context> contextSupplier) {
        var context = contextSupplier.get();
        var player = NetworkUtil.getPlayerInPhysicalServer(playerID, context);
        if (player == null) return;
        handle(player, context);
    }

    public void handle(Player player, NetworkEvent.Context context) {
        if (context.getDirection().getReceptionSide() == LogicalSide.SERVER) {
            // When receive packet from client, permit packet change only the sender player's information
            if (!player.getUUID().equals(playerID)) return;
            // Unable to overwrite ServerLimitation on Server
            if (serverLimitation()) return;
        } else {
            // Unable to overwrite local player's ClientLimitation
            if (clientLimitation() && player.isLocalPlayer()) return;
        }

        var parkourability = Parkourability.get(player);
        if (serverLimitation) {
            parkourability.getActionInfo().setServerLimitation(limitation);
        } else {
            parkourability.getActionInfo().setClientLimitation(limitation);
        }

        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            if (requestReply) {
                var localPlayer = Minecraft.getInstance().player;
                if (localPlayer == null) return;
                // send ClientLimitation if requested
                ParCool.CONNECTION.reply(
                        new LimitationPacket(
                                localPlayer.getUUID(), false, false,
                                CompiledLimitation.compile(Limitation.readFromConfig(
                                        ParCoolConfig.getClientConfigLimitation(),
                                        ParCool.getActionRegistry(),
                                        ParCool.getStaminaTypeRegistry()
                                ))
                        ),
                        context
                );
            }
        } else {
            ParCool.CONNECTION.send(PacketDistributor.ALL.noArg(), this.noResponse()); // share ClientLimitation
            if (requestReply) { // send ServerLimitation if requested
                var sender = context.getSender();
                if (sender == null) return;
                ParCool.CONNECTION.reply(new LimitationPacket(sender.getUUID(), true, false, ParCool.getLimitationRegistry().getLimitationSet(sender.getUUID())), context);
            }
        }
    }

    public static void logReceived(Player player) {
        ParCool.LOGGER.log(Level.INFO, "Received Server Limitation of [{}]", player.getGameProfile().getName());
    }

    public static void logSent(Player player) {
        ParCool.LOGGER.log(Level.INFO, "Sent Server Limitation of [{}]", player.getGameProfile().getName());
    }
}
