package com.alrex.parcool.common.network;

public class MultiStaminaPacket extends MultiComposablePacket<StaminaPacket> {
    public MultiStaminaPacket() {
        super(StaminaPacket.HANDLER);
    }
}
