package com.alrex.parcool.common.network;

import com.alrex.parcool.common.info.CompiledLimitation;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ClientBoundParCoolLoginPacket(CompiledLimitation limitation) {
    public void encode(FriendlyByteBuf packet) {
        limitation.writeTo(packet);
    }

    public static ClientBoundParCoolLoginPacket decode(FriendlyByteBuf packet) {
        return new ClientBoundParCoolLoginPacket(CompiledLimitation.readFrom(packet));
    }

    @OnlyIn(Dist.CLIENT)
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
    }
}
