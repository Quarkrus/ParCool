package com.alrex.parcool.common.info;

import com.alrex.parcool.server.limitation.ILimitationEntry;
import com.alrex.parcool.server.limitation.Limitation;
import com.alrex.parcool.server.limitation.LimitationEntries;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Collection;

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
        public boolean isSynced() {
            return false;
        }
    }

    private static class Remote extends CompiledLimitation {
        private final boolean[] booleans = new boolean[LimitationEntries.Bool.ENTRIES.size()];
        private final short[] integers = new short[LimitationEntries.Int.ENTRIES.size()];
        private final float[] reals = new float[LimitationEntries.Real.ENTRIES.size()];
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
        public boolean isSynced() {
            return true;
        }
    }

    public static final CompiledLimitation UNSYNCED_INSTANCE = new Default();

    public abstract boolean get(ILimitationEntry.Bool entry);

    public abstract short get(ILimitationEntry.Int entry);

    public abstract float get(ILimitationEntry.Real entry);

    public abstract boolean isSynced();

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
        }
        return instance;
    }
}
