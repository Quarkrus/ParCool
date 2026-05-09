package com.alrex.parcool.common.info;

import com.alrex.parcool.common.action.ActionEntry;
import com.alrex.parcool.server.limitation.ActionLimitationValue;
import com.alrex.parcool.server.limitation.ILimitationEntry;
import com.alrex.parcool.server.limitation.Limitation;
import com.alrex.parcool.server.limitation.LimitationEntries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public abstract class CompiledLimitation {
    private static class Default extends CompiledLimitation {
        @Override
        public boolean get(ILimitationEntry.Bool entry) {
            return entry.defaultValue();
        }

        @Override
        public short get(ILimitationEntry.Int entry) {
            return entry.defaultValue();
        }

        @Override
        public float get(ILimitationEntry.Real entry) {
            return entry.defaultValue();
        }

        @Override
        public ActionLimitationValue get(ActionEntry<?> entry) {
            return ActionLimitationValue.NO_LIMIT;
        }

        @Nullable
        @Override
        public ResourceLocation getStaminaType() {
            return null;
        }

        @Override
        public boolean isSynced() {
            return false;
        }

        @Override
        protected Map<String, ? extends Map<Short, ActionLimitationValue>> getActionsMap() {
            return Collections.emptyMap();
        }
    }

    private static class Remote extends CompiledLimitation {
        private final boolean[] booleans = new boolean[LimitationEntries.Bool.ENTRIES.size()];
        private final short[] integers = new short[LimitationEntries.Int.ENTRIES.size()];
        private final float[] reals = new float[LimitationEntries.Real.ENTRIES.size()];
        private final TreeMap<String, TreeMap<Short, ActionLimitationValue>> actions = new TreeMap<>();
        private ResourceLocation staminaTypeID = null;
        @Override
        public boolean get(ILimitationEntry.Bool entry) {
            var index = entry.index();
            if (0 <= index && index < booleans.length) return booleans[index];
            return entry.defaultValue();
        }

        @Override
        public short get(ILimitationEntry.Int entry) {
            var index = entry.index();
            if (0 <= index && index < integers.length) return integers[index];
            return entry.defaultValue();
        }

        @Override
        public float get(ILimitationEntry.Real entry) {
            var index = entry.index();
            if (0 <= index && index < reals.length) return reals[index];
            return entry.defaultValue();
        }

        @Override
        public ActionLimitationValue get(ActionEntry<?> entry) {
            var map = actions.get(entry.id().getNamespace());
            if (map == null) return ActionLimitationValue.NO_LIMIT;
            var value = map.get(entry.index());
            if (value == null) return ActionLimitationValue.NO_LIMIT;
            return value;
        }

        @Nullable
        @Override
        public ResourceLocation getStaminaType() {
            return staminaTypeID;
        }

        @Override
        public boolean isSynced() {
            return true;
        }

        @Override
        protected Map<String, ? extends Map<Short, ActionLimitationValue>> getActionsMap() {
            return actions;
        }
    }

    public static final CompiledLimitation UNSYNCED_INSTANCE = new Default();

    public abstract boolean get(ILimitationEntry.Bool entry);

    public abstract short get(ILimitationEntry.Int entry);

    public abstract float get(ILimitationEntry.Real entry);

    public abstract ActionLimitationValue get(ActionEntry<?> entry);

    @Nullable
    public abstract ResourceLocation getStaminaType();

    public abstract boolean isSynced();

    protected abstract Map<String, ? extends Map<Short, ActionLimitationValue>> getActionsMap();

    public void writeTo(FriendlyByteBuf buffer) {
        for (var entry : LimitationEntries.Bool.ENTRIES) {
            buffer.writeBoolean(this.get(entry));
        }
        for (var entry : LimitationEntries.Int.ENTRIES) {
            buffer.writeShort(this.get(entry));
        }
        for (var entry : LimitationEntries.Real.ENTRIES) {
            buffer.writeFloat(this.get(entry));
        }
        var staminaID = getStaminaType();
        if (staminaID == null) {
            buffer.writeByte(0);
        } else {
            var staminaIDStr = staminaID.toString();
            buffer.writeByte(staminaIDStr.length());
            buffer.writeCharSequence(staminaIDStr, StandardCharsets.US_ASCII);
        }
        var actionsMap = getActionsMap();
        buffer.writeByte(actionsMap.size());
        for (var actionsInGroup : actionsMap.entrySet()) {
            buffer.writeByte(actionsInGroup.getKey().length());
            buffer.writeCharSequence(actionsInGroup.getKey(), StandardCharsets.US_ASCII);
            buffer.writeShort(getActionsMap().size());
            for (var actionValue : actionsInGroup.getValue().entrySet()) {
                buffer.writeShort(actionValue.getKey());
                actionValue.getValue().writeTo(buffer);
            }
        }
    }

    public static CompiledLimitation readFrom(FriendlyByteBuf buffer) {
        Remote instance = new Remote();
        for (var entry : LimitationEntries.Bool.ENTRIES) {
            instance.booleans[entry.index()] = buffer.readBoolean();
        }
        for (var entry : LimitationEntries.Int.ENTRIES) {
            instance.integers[entry.index()] = buffer.readShort();
        }
        for (var entry : LimitationEntries.Real.ENTRIES) {
            instance.reals[entry.index()] = buffer.readFloat();
        }
        var staminaStrLen = buffer.readByte();
        if (staminaStrLen > 0) {
            instance.staminaTypeID = ResourceLocation.tryParse(buffer.readCharSequence(staminaStrLen, StandardCharsets.US_ASCII).toString());
        }
        var actionGroupSize = buffer.readByte();
        for (int i = 0; i < actionGroupSize; i++) {
            var groupNameSize = buffer.readByte();
            var groupName = buffer.readCharSequence(groupNameSize, StandardCharsets.US_ASCII).toString();
            var syncedActionSize = buffer.readShort();
            for (int j = 0; j < syncedActionSize; j++) {
                var actionID = buffer.readShort();
                instance.actions
                        .computeIfAbsent(groupName, (k) -> new TreeMap<>())
                        .put(actionID, ActionLimitationValue.readFrom(buffer));
            }
        }
        return instance;
    }

    public static CompiledLimitation compile(Collection<Limitation> limitations) {
        Remote instance = new Remote();
        for (var entry : LimitationEntries.Bool.ENTRIES) {
            instance.booleans[entry.index()] = entry.getLowestPriorityValue();
        }
        for (var entry : LimitationEntries.Int.ENTRIES) {
            instance.integers[entry.index()] = entry.getLowestPriorityValue();
        }
        for (var entry : LimitationEntries.Real.ENTRIES) {
            instance.reals[entry.index()] = entry.getLowestPriorityValue();
        }
        for (var limitation : limitations) {
            for (var entry : LimitationEntries.Bool.ENTRIES) {
                instance.booleans[entry.index()] = entry.select(instance.booleans[entry.index()], limitation.get(entry));
            }
            for (var entry : LimitationEntries.Int.ENTRIES) {
                instance.integers[entry.index()] = entry.select(instance.integers[entry.index()], limitation.get(entry));
            }
            for (var entry : LimitationEntries.Real.ENTRIES) {
                instance.reals[entry.index()] = entry.select(instance.reals[entry.index()], limitation.get(entry));
            }
            for (var entry : limitation.actions().entrySet()) {
                var currentValue = instance.get(entry.getKey());
                var limitationValue = entry.getValue();
                if (limitationValue == ActionLimitationValue.NO_LIMIT) continue;
                instance.actions
                        .computeIfAbsent(entry.getKey().id().getNamespace(), (k) -> new TreeMap<>())
                        .put(entry.getKey().index(), ActionLimitationValue.compile(currentValue, limitationValue));
            }
        }
        for (var limitation : limitations) {
            var staminaType = limitation.getStaminaType();
            if (staminaType == null) continue;
            instance.staminaTypeID = staminaType;
            break;
        }
        return instance;
    }
}
