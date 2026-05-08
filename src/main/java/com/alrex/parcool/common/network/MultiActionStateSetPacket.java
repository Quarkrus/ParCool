package com.alrex.parcool.common.network;

public class MultiActionStateSetPacket extends MultiComposablePacket<ActionStateSetPacket> {
    public MultiActionStateSetPacket() {
        super(ActionStateSetPacket.HANDLER);
    }
}
