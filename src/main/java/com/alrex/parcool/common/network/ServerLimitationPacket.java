package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.info.CompiledLimitation;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import org.apache.logging.log4j.Level;

import java.util.function.Supplier;

public record ServerLimitationPacket(CompiledLimitation limitation) {

    public void encode(FriendlyByteBuf packet) {
        limitation.writeTo(packet);
    }

    public static ServerLimitationPacket decode(FriendlyByteBuf packet) {
        return new ServerLimitationPacket(CompiledLimitation.readFrom(packet));
    }

    @OnlyIn(Dist.CLIENT)
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            //TODO
        });
        contextSupplier.get().setPacketHandled(true);
    }

    public static void logReceived(Player player) {
        ParCool.LOGGER.log(Level.INFO, "Received Server Limitation of [{}]", player.getGameProfile().getName());
    }

    public static void logSent(Player player) {
        ParCool.LOGGER.log(Level.INFO, "Sent Server Limitation of [{}]", player.getGameProfile().getName());
    }
}
