package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.alrex.parcool.common.Parkourability;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.LogicalSide;
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
            Player player = contextSupplier.get().getSender();
            if (player == null) return;
            if (!player.getUUID().equals(actionStateSetPacket.playerID)) {
                player = player.getLevel().getPlayerByUUID(actionStateSetPacket.playerID);
            }
            processPlayer(actionStateSetPacket, player);
            ParCool.getActionProcessor().getActionSyncDepot().requestSync(actionStateSetPacket);
        }

        @Override
        public void handleInPhysicalClient(ActionStateSetPacket actionStateSetPacket, Supplier<NetworkEvent.Context> contextSupplier) {
            Player player;
            boolean isInLogicalServer = contextSupplier.get().getDirection().getReceptionSide() == LogicalSide.SERVER;
            if (isInLogicalServer) {
                player = contextSupplier.get().getSender();
                if (player == null) return;
                if (!player.getUUID().equals(actionStateSetPacket.playerID)) {
                    player = player.getLevel().getPlayerByUUID(actionStateSetPacket.playerID);
                }
            } else {
                var world = Minecraft.getInstance().level;
                if (world == null) return;
                player = world.getPlayerByUUID(actionStateSetPacket.playerID);
            }
            if (player == null) return;
            processPlayer(actionStateSetPacket, player);
            if (isInLogicalServer) {
                ParCool.getActionProcessor().getActionSyncDepot().requestSync(actionStateSetPacket);
            }
        }

        private void processPlayer(ActionStateSetPacket packet, Player player) {
            var parkourability = Parkourability.get(player);
            for (var subPacket : packet.getSubPacket()) {
                for (var syncEntry : subPacket.entries()) {
                    var action = parkourability.get(syncEntry.entry());
                    action.getSynchronizedData().acceptPacket(syncEntry);
                    if (syncEntry.type() == ActionStatePacket.Type.START) {
                        MinecraftForge.EVENT_BUS.post(new ParCoolActionEvent.Start.Pre(parkourability.player(), action));
                        action.start();
                        MinecraftForge.EVENT_BUS.post(new ParCoolActionEvent.Start.Post(parkourability.player(), action));
                    } else if (syncEntry.type() == ActionStatePacket.Type.FINISH) {
                        MinecraftForge.EVENT_BUS.post(new ParCoolActionEvent.Finish.Pre(parkourability.player(), action));
                        action.finish();
                        MinecraftForge.EVENT_BUS.post(new ParCoolActionEvent.Finish.Post(parkourability.player(), action));
                    }
                }
            }
        }
    }
}
