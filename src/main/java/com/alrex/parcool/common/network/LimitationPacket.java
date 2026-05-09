package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.info.CompiledLimitation;
import com.alrex.parcool.util.NetworkUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import org.apache.logging.log4j.Level;

import java.util.UUID;
import java.util.function.Supplier;

public record LimitationPacket(UUID playerID, boolean serverLimitation, CompiledLimitation limitation) {

    public void encode(FriendlyByteBuf packet) {
        packet.writeUUID(playerID);
        packet.writeBoolean(serverLimitation);
        limitation.writeTo(packet);
    }

    public static LimitationPacket decode(FriendlyByteBuf packet) {
        return new LimitationPacket(packet.readUUID(), packet.readBoolean(), CompiledLimitation.readFrom(packet));
    }

    @OnlyIn(Dist.CLIENT)
    public void handleInPhysicalClient(Supplier<NetworkEvent.Context> contextSupplier) {
        var player = NetworkUtil.getPlayerInPhysicalClient(playerID, contextSupplier.get());
        if (player == null) return;
        setLimitation(player);
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    public void handleInPhysicalServer(Supplier<NetworkEvent.Context> contextSupplier) {
        var player = NetworkUtil.getPlayerInPhysicalServer(playerID, contextSupplier.get());
        if (player == null) return;
        setLimitation(player);
    }

    private void setLimitation(Player player) {
        var parkourability = Parkourability.get(player);
        if (serverLimitation) {
            parkourability.getActionInfo().setServerLimitation(limitation);
        } else {
            parkourability.getActionInfo().setClientLimitation(limitation);
        }
    }

    public static void logReceived(Player player) {
        ParCool.LOGGER.log(Level.INFO, "Received Server Limitation of [{}]", player.getGameProfile().getName());
    }

    public static void logSent(Player player) {
        ParCool.LOGGER.log(Level.INFO, "Sent Server Limitation of [{}]", player.getGameProfile().getName());
    }
}
