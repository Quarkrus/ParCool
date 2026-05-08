package com.alrex.parcool.common.network;

import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

public class ActionStateSetPacket extends MultiComposablePacket<ActionStatePacket> {
    private final UUID playerID;

    public ActionStateSetPacket(UUID playerID) {
        super(ActionStatePacket.HANDLER);
        this.playerID = playerID;
    }

    public static ActionStateSetPacket decode(FriendlyByteBuf packet) {
        var id = new UUID(packet.readLong(), packet.readLong());
        return ActionStateSetPacket.decode(() -> new ActionStateSetPacket(id), packet);
    }

    public static void encode(ActionStateSetPacket actionStateSetPacket, FriendlyByteBuf packet) {
        packet.writeLong(actionStateSetPacket.playerID.getMostSignificantBits());
        packet.writeLong(actionStateSetPacket.playerID.getLeastSignificantBits());
        MultiComposablePacket.encode(actionStateSetPacket, packet);
    }
}
