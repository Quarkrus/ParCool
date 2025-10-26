package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.stamina.OtherStamina;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.Supplier;

public class SyncStaminaToClientMessage {
    // Please access these request and tick methods from only server's main thread, cuz it's not thread safe
    public static void requestSync(ServerPlayerEntity player) {
        IStamina stamina = IStamina.get(player);
        if (!(stamina instanceof OtherStamina)) return;
        requestSync(player, (OtherStamina) stamina);
    }

    public static void requestSync(ServerPlayerEntity player, OtherStamina stamina) {
        requestSync(player.getUUID(), stamina);
    }

    public static void requestSync(UUID playerID, OtherStamina stamina) {
        SynchronizeRequestRegistry.getInstance().add(playerID, SynchronizedState.create(stamina));
    }

    private static int syncCooldownTick = 0;

    public static void tick() {
        if (syncCooldownTick > 0) {
            syncCooldownTick--;
        } else if (SynchronizeRequestRegistry.getInstance().isNotEmpty()) {
            syncCooldownTick = 10;
            SyncStaminaToClientMessage message = new SyncStaminaToClientMessage(SynchronizeRequestRegistry.getInstance().getAndClearRequestedStates());
            ParCool.CHANNEL_INSTANCE.send(PacketDistributor.ALL.noArg(), message);
        }
    }

    private static class SynchronizeRequestRegistry {
        private SynchronizeRequestRegistry() {
        }

        @Nullable
        private static SynchronizeRequestRegistry instance = null;

        public static SynchronizeRequestRegistry getInstance() {
            if (instance == null) instance = new SynchronizeRequestRegistry();
            return instance;
        }

        @Nonnull
        private TreeMap<UUID, SynchronizedState> idAndStateMap = new TreeMap<>();

        public void add(UUID playerID, SynchronizedState state) {
            idAndStateMap.put(playerID, state);
        }

        public boolean isNotEmpty() {
            return !idAndStateMap.isEmpty();
        }

        public TreeMap<UUID, SynchronizedState> getAndClearRequestedStates() {
            TreeMap<UUID, SynchronizedState> map = idAndStateMap;
            idAndStateMap = new TreeMap<>();
            return map;
        }
    }

    private static class SynchronizedState {
        private int staminaValue = 0;
        private int maxValue = 0;
        private boolean exhausted = false;
        private boolean imposingPenalty = false;

        public SynchronizedState(int staminaValue, int maxValue, boolean exhausted, boolean imposingPenalty) {
            this.staminaValue = staminaValue;
            this.maxValue = maxValue;
            this.exhausted = exhausted;
            this.imposingPenalty = imposingPenalty;
        }

        public void encode(PacketBuffer buffer) {
            buffer.writeInt(this.staminaValue);
            buffer.writeInt(this.maxValue);
            buffer.writeBoolean(this.exhausted);
            buffer.writeBoolean(this.imposingPenalty);
        }

        public static SynchronizedState load(PacketBuffer buffer) {
            return new SynchronizedState(buffer.readInt(), buffer.readInt(), buffer.readBoolean(), buffer.readBoolean());
        }

        public static SynchronizedState create(OtherStamina stamina) {
            return new SynchronizedState(stamina.get(), stamina.getActualMaxStamina(), stamina.isExhausted(), stamina.isImposingExhaustionPenalty());
        }

        public void apply(OtherStamina stamina) {
            stamina.setMax(this.maxValue);
            stamina.set(this.staminaValue);
            stamina.setImposingPenalty(this.imposingPenalty);
            stamina.setExhaustion(this.exhausted);
        }
    }

    private final Map<UUID, SynchronizedState> idAndStateMap;

    private SyncStaminaToClientMessage(Map<UUID, SynchronizedState> stateMap) {
        idAndStateMap = stateMap;
    }

    public void encode(PacketBuffer packet) {
        this.idAndStateMap.forEach((id, state) -> {
            packet.writeLong(id.getMostSignificantBits());
            packet.writeLong(id.getLeastSignificantBits());
            state.encode(packet);
        });
    }

    @OnlyIn(Dist.CLIENT)
    public static SyncStaminaToClientMessage decode(PacketBuffer packet) {
        TreeMap<UUID, SynchronizedState> map = new TreeMap<>();
        while (packet.isReadable()) {
            map.put(
                    new UUID(packet.readLong(), packet.readLong()),
                    SynchronizedState.load(packet)
            );
        }
        return new SyncStaminaToClientMessage(map);
    }

    @OnlyIn(Dist.CLIENT)
    public void handleClient(Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            World world = Minecraft.getInstance().level;
            if (world == null) return;
            this.idAndStateMap.forEach((id, state) -> {
                PlayerEntity player = world.getPlayerByUUID(id);
                if (player == null) return;
                if (player.isLocalPlayer()) return;

                IStamina stamina = IStamina.get(player);
                if (!(stamina instanceof OtherStamina)) return;
                state.apply((OtherStamina) stamina);
            });
        });
        contextSupplier.get().setPacketHandled(true);
    }
}
