package com.alrex.parcool.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Supplier;

public abstract class MultiComposablePacket<T> {
    protected final LinkedList<T> msgList = new LinkedList<>();
    protected final IHandler<T> handler;

    protected MultiComposablePacket(IHandler<T> handler) {
        this.handler = handler;
    }

    public void add(T packet) {
        msgList.add(packet);
    }

    protected Collection<T> getSubPacket() {
        return msgList;
    }

    public static <U, V extends MultiComposablePacket<U>> void encode(V msg, FriendlyByteBuf packet) {
        for (var singleMsg : msg.msgList) {
            msg.handler.encode(singleMsg, packet);
        }
    }

    public static <U, V extends MultiComposablePacket<U>> V decode(Supplier<V> msg, FriendlyByteBuf packet) {
        var instance = msg.get();
        instance.add(instance.handler.decode(packet));
        return instance;
    }

    public static <U, V extends MultiComposablePacket<U>> void handleInPhysicalClient(V msg, Supplier<NetworkEvent.Context> contextSupplier) {
        for (var singleMsg : msg.msgList) {
            msg.handler.handleInPhysicalClient(singleMsg, contextSupplier);
        }
    }

    public static <U, V extends MultiComposablePacket<U>> void handleInPhysicalServer(V msg, Supplier<NetworkEvent.Context> contextSupplier) {
        for (var singleMsg : msg.msgList) {
            msg.handler.handleInPhysicalServer(singleMsg, contextSupplier);
        }
    }
}
