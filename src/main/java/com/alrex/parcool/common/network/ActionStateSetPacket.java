package com.alrex.parcool.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class ActionStateSetPacket extends MultiComposablePacket<ActionStatePacket> {
    private final UUID playerID;
    public static final IHandler<ActionStateSetPacket> HANDLER = new Handler();

    public ActionStateSetPacket(UUID playerID) {
        super(ActionStatePacket.HANDLER);
        this.playerID = playerID;
    }

    private static class Handler implements IHandler<ActionStateSetPacket> {
        @Override
        public void encode(ActionStateSetPacket actionStateSetPacket, FriendlyByteBuf packet) {
            packet.writeLong(actionStateSetPacket.playerID.getMostSignificantBits());
            packet.writeLong(actionStateSetPacket.playerID.getLeastSignificantBits());
            MultiComposablePacket.encode(actionStateSetPacket, packet);
        }

        @Override
        public ActionStateSetPacket decode(FriendlyByteBuf packet) {
            var id = new UUID(packet.readLong(), packet.readLong());
            return ActionStateSetPacket.decode(() -> new ActionStateSetPacket(id), packet);
        }

        @Override
        public void handleInPhysicalServer(ActionStateSetPacket actionStateSetPacket, Supplier<NetworkEvent.Context> contextSupplier) {
            ActionStateSetPacket.handleInPhysicalServer(actionStateSetPacket, contextSupplier);

        }

        @Override
        public void handleInPhysicalClient(ActionStateSetPacket actionStateSetPacket, Supplier<NetworkEvent.Context> contextSupplier) {
            ActionStateSetPacket.handleInPhysicalClient(actionStateSetPacket, contextSupplier);
        }
    }
}
