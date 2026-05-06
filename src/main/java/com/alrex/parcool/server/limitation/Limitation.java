package com.alrex.parcool.server.limitation;

import com.alrex.parcool.config.ParCoolConfig;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.TreeMap;

public abstract class Limitation {

    //Whether this limitation is applied
    private boolean enabled = false;
    private final ID id;

    private Limitation(ID id) {
        this.id = id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ID getID() {
        return id;
    }

    public abstract boolean get(ILimitationEntry.Bool entry);

    public abstract short get(ILimitationEntry.Int entry);

    public abstract double get(ILimitationEntry.Real entry);

    public abstract void set(ILimitationEntry.Bool entry, boolean value);

    public abstract void set(ILimitationEntry.Int entry, short value);

    public abstract void set(ILimitationEntry.Real entry, float value);

    public abstract JsonObject save();

    public abstract void reset();

    private static class PartialLimitation extends Limitation {
        private final TreeMap<Integer, Boolean> booleans = new TreeMap<>();
        private final TreeMap<Integer, Short> integers = new TreeMap<>();
        private final TreeMap<Integer, Float> reals = new TreeMap<>();

        public PartialLimitation(ID id) {
            super(id);
        }

        public boolean get(ILimitationEntry.Bool entry) {
            var value = booleans.get(entry.index());
            return value != null ? value : entry.defaultValue();
        }

        public short get(ILimitationEntry.Int entry) {
            var value = integers.get(entry.index());
            return value != null ? value : entry.defaultValue();
        }

        public double get(ILimitationEntry.Real entry) {
            var value = reals.get(entry.index());
            return value != null ? value : entry.defaultValue();
        }

        public void set(ILimitationEntry.Bool entry, boolean value) {
            booleans.put(entry.index(), value);
        }

        public void set(ILimitationEntry.Int entry, short value) {
            integers.put(entry.index(), value);
        }

        public void set(ILimitationEntry.Real entry, float value) {
            reals.put(entry.index(), value);
        }

        public void reset() {
            booleans.clear();
            integers.clear();
            reals.clear();
        }

        @Override
        public JsonObject save() {
            var baseObj = new JsonObject();
            baseObj.add("enabled", new JsonPrimitive(isEnabled()));
            var booleansObj = new JsonObject();
            for (var boolValues : booleans.entrySet()) {
                if (0 <= boolValues.getKey() && boolValues.getKey() < LimitationEntries.Bool.ENTRIES.size()) {
                    booleansObj.add(LimitationEntries.Bool.ENTRIES.get(boolValues.getKey()).name(), new JsonPrimitive(boolValues.getValue()));
                }
            }
            var intObj = new JsonObject();
            for (var intValues : integers.entrySet()) {
                if (0 <= intValues.getKey() && intValues.getKey() < LimitationEntries.Int.ENTRIES.size()) {
                    intObj.add(LimitationEntries.Int.ENTRIES.get(intValues.getKey()).name(), new JsonPrimitive(intValues.getValue()));
                }
            }
            var realObj = new JsonObject();
            for (var realValues : integers.entrySet()) {
                if (0 <= realValues.getKey() && realValues.getKey() < LimitationEntries.Real.ENTRIES.size()) {
                    realObj.add(LimitationEntries.Real.ENTRIES.get(realValues.getKey()).name(), new JsonPrimitive(realValues.getValue()));
                }
            }
            baseObj.add("bool", booleansObj);
            baseObj.add("int", intObj);
            baseObj.add("real", realObj);
            return baseObj;
        }
    }

    private static class FullLimitation extends Limitation {
        private final boolean[] booleans = new boolean[LimitationEntries.Bool.ENTRIES.size()];
        private final short[] integers = new short[LimitationEntries.Int.ENTRIES.size()];
        private final float[] reals = new float[LimitationEntries.Real.ENTRIES.size()];

        public FullLimitation(ID id) {
            super(id);
            reset();
        }

        public boolean get(ILimitationEntry.Bool entry) {
            var index = entry.index();
            if (0 <= index && index < booleans.length) return booleans[index];
            return entry.defaultValue();
        }

        public short get(ILimitationEntry.Int entry) {
            var index = entry.index();
            if (0 <= index && index < integers.length) return integers[index];
            return entry.defaultValue();
        }

        public double get(ILimitationEntry.Real entry) {
            var index = entry.index();
            if (0 <= index && index < reals.length) return reals[index];
            return entry.defaultValue();
        }

        public void set(ILimitationEntry.Bool entry, boolean value) {
            var index = entry.index();
            if (0 <= index && index < booleans.length) booleans[index] = value;
        }

        public void set(ILimitationEntry.Int entry, short value) {
            var index = entry.index();
            if (0 <= index && index < integers.length) integers[index] = value;
        }

        public void set(ILimitationEntry.Real entry, float value) {
            var index = entry.index();
            if (0 <= index && index < reals.length) reals[index] = value;
        }

        public void reset() {
            for (var entry : LimitationEntries.Bool.ENTRIES) {
                booleans[entry.index()] = switch (entry.priority()) {
                    case LOWER -> true;
                    case HIGHER -> false;
                    case NONE -> entry.defaultValue();
                };
            }
            for (var entry : LimitationEntries.Int.ENTRIES) {
                integers[entry.index()] = switch (entry.priority()) {
                    case LOWER -> entry.max();
                    case HIGHER -> entry.min();
                    case NONE -> entry.defaultValue();
                };
            }
            for (var entry : LimitationEntries.Real.ENTRIES) {
                reals[entry.index()] = switch (entry.priority()) {
                    case LOWER -> entry.max();
                    case HIGHER -> entry.min();
                    case NONE -> entry.defaultValue();
                };
            }
        }

        @Override
        public JsonObject save() {
            var baseObj = new JsonObject();
            baseObj.add("enabled", new JsonPrimitive(isEnabled()));
            var booleansObj = new JsonObject();
            for (int i = 0; i < booleans.length; i++) {
                booleansObj.add(LimitationEntries.Bool.ENTRIES.get(i).name(), new JsonPrimitive(booleans[i]));
            }
            var intObj = new JsonObject();
            for (int i = 0; i < integers.length; i++) {
                intObj.add(LimitationEntries.Int.ENTRIES.get(i).name(), new JsonPrimitive(integers[i]));
            }
            var realObj = new JsonObject();
            for (int i = 0; i < reals.length; i++) {
                realObj.add(LimitationEntries.Real.ENTRIES.get(i).name(), new JsonPrimitive(reals[i]));
            }
            baseObj.add("bool", booleansObj);
            baseObj.add("int", intObj);
            baseObj.add("real", realObj);
            return baseObj;
        }
    }

    public static Limitation newEmptyInstance(ID id) {
        return new PartialLimitation(id);
    }

    public static Limitation readFromServerConfig(ParCoolConfig.ConfigLimitation configLimitation) {
        var limitation = new FullLimitation(LimitationRegistry.GLOBAL_ID);
        limitation.setEnabled(configLimitation.isEnabled());
        for (var entry : LimitationEntries.Bool.ENTRIES) {
            limitation.set(entry, configLimitation.get(entry).get());
        }
        for (var entry : LimitationEntries.Int.ENTRIES) {
            limitation.set(entry, configLimitation.get(entry).get().shortValue());
        }
        for (var entry : LimitationEntries.Real.ENTRIES) {
            limitation.set(entry, configLimitation.get(entry).get().floatValue());
        }
        return limitation;
    }

    public static Limitation readFrom(ID id, JsonObject object) {
        var limitation = new PartialLimitation(id);
        if (object.has("enabled")) {
            var value = object.get("enabled");
            if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean()) {
                limitation.setEnabled(value.getAsBoolean());
            }
        }
        if (object.has("bool")) {
            var value = object.get("bool");
            if (value instanceof JsonObject valueObj) {
                for (var entry : LimitationEntries.Bool.ENTRIES) {
                    if (valueObj.has(entry.name()) && valueObj.get(entry.name()) instanceof JsonPrimitive boolValue && boolValue.isBoolean()) {
                        limitation.set(entry, boolValue.getAsBoolean());
                    }
                }
            }
        }
        if (object.has("int")) {
            var value = object.get("int");
            if (value instanceof JsonObject valueObj) {
                for (var entry : LimitationEntries.Int.ENTRIES) {
                    if (valueObj.has(entry.name()) && valueObj.get(entry.name()) instanceof JsonPrimitive intValue && intValue.isNumber()) {
                        limitation.set(entry, intValue.getAsShort());
                    }
                }
            }
        }
        if (object.has("real")) {
            var value = object.get("real");
            if (value instanceof JsonObject valueObj) {
                for (var entry : LimitationEntries.Real.ENTRIES) {
                    if (valueObj.has(entry.name()) && valueObj.get(entry.name()) instanceof JsonPrimitive realValue && realValue.isNumber()) {
                        limitation.set(entry, realValue.getAsFloat());
                    }
                }
            }
        }
        return limitation;
    }

    public record ID(String group, String name) implements Comparable<ID> {
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ID other) {
                return group.equals(other.group) && name.equals(other.name);
            }
            return false;
        }

        @Override
        public int compareTo(ID o) {
            int groupCompare = group.compareTo(o.group);
            if (groupCompare != 0) return groupCompare;
            return name.compareTo(o.name);
        }
    }
}
