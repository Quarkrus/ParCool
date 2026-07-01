package com.alrex.parcool.server.limitation;

import com.alrex.parcool.api.action.ActionEntry;
import com.alrex.parcool.common.action.ActionRegistry;
import com.alrex.parcool.common.stamina.StaminaTypeRegistry;
import com.alrex.parcool.config.ParCoolConfig;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.TreeMap;

public abstract class Limitation implements ILimitation {

    //Whether this limitation is applied
    private boolean enabled = false;
    private final ID id;
    @Nullable
    protected ResourceLocation staminaTypeEntry;
    protected final TreeMap<ActionEntry<?>, ActionLimitationValue> actions = new TreeMap<>();

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

    public abstract void set(ILimitationEntry.Bool entry, boolean value);

    public abstract void set(ILimitationEntry.Int entry, short value);

    public abstract void set(ILimitationEntry.Real entry, float value);

    public abstract void set(ActionEntry<?> entry, ActionLimitationValue value);

    public abstract JsonObject save();

    public abstract void reset();

    @Nullable
    @Override
    public ResourceLocation getStaminaType() {
        return staminaTypeEntry;
    }

    @Override
    public Map<ActionEntry<?>, ActionLimitationValue> actions() {
        return actions;
    }

    public void setStaminaType(ResourceLocation id) {
        staminaTypeEntry = id;
    }

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

        public float get(ILimitationEntry.Real entry) {
            var value = reals.get(entry.index());
            return value != null ? value : entry.defaultValue();
        }

        @Nullable
        public ActionLimitationValue get(ActionEntry<?> entry) {
            return actions.get(entry);
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

        public void set(ActionEntry<?> entry, ActionLimitationValue value) {
            actions.put(entry, value);
        }

        public void reset() {
            booleans.clear();
            integers.clear();
            reals.clear();
            actions.clear();
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
            var actionsObj = new JsonObject();
            for (var actionValues : actions.entrySet()) {
                if (actionValues.getValue().isDefault(actionValues.getKey())) continue;
                actionsObj.add(
                        actionValues.getKey().id().toString(),
                        actionValues.getValue().writeToJson(actionValues.getKey())
                );
            }
            baseObj.add("bool", booleansObj);
            baseObj.add("int", intObj);
            baseObj.add("real", realObj);
            baseObj.add("action", actionsObj);
            if (staminaTypeEntry != null) {
                baseObj.add("stamina", new JsonPrimitive(staminaTypeEntry.toString()));
            }
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

        public float get(ILimitationEntry.Real entry) {
            var index = entry.index();
            if (0 <= index && index < reals.length) return reals[index];
            return entry.defaultValue();
        }

        @Nullable
        public ActionLimitationValue get(ActionEntry<?> entry) {
            return actions.get(entry);
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

        @Override
        public void set(ActionEntry<?> entry, ActionLimitationValue value) {
            actions.put(entry, value);
        }

        public void reset() {
            for (var entry : LimitationEntries.Bool.ENTRIES) {
                booleans[entry.index()] = entry.getLowestPriorityValue();
            }
            for (var entry : LimitationEntries.Int.ENTRIES) {
                integers[entry.index()] = entry.getLowestPriorityValue();
            }
            for (var entry : LimitationEntries.Real.ENTRIES) {
                reals[entry.index()] = entry.getLowestPriorityValue();
            }
            actions.clear();
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
            var actionsObj = new JsonObject();
            for (var actionValues : actions.entrySet()) {
                if (actionValues.getValue().isDefault(actionValues.getKey())) continue;
                actionsObj.add(
                        actionValues.getKey().id().toString(),
                        actionValues.getValue().writeToJson(actionValues.getKey())
                );
            }
            baseObj.add("bool", booleansObj);
            baseObj.add("int", intObj);
            baseObj.add("real", realObj);
            baseObj.add("action", actionsObj);
            if (staminaTypeEntry != null) {
                baseObj.add("stamina", new JsonPrimitive(staminaTypeEntry.toString()));
            }
            return baseObj;
        }
    }

    public static Limitation newEmptyInstance(ID id) {
        return new PartialLimitation(id);
    }

    public static Limitation readFromConfig(ParCoolConfig.ConfigLimitation configLimitation, ActionRegistry actionRegistry, StaminaTypeRegistry staminaRegistry) {
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
        for (var actionGroup : actionRegistry.getRegisteredGroups().entrySet()) {
            for (var action : actionGroup.getValue().actions()) {
                limitation.set(action, configLimitation.get(action).asLimitationValue());
            }
        }
        var staminaID = ResourceLocation.tryParse(configLimitation.getStaminaTypeID().get());
        if (staminaID != null) {
            limitation.setStaminaType(staminaID);
        }
        return limitation;
    }

    public static Limitation readFrom(ID id, JsonObject object, ActionRegistry registry, StaminaTypeRegistry staminaRegistry) {
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
        if (object.has("action")) {
            var value = object.get("action");
            if (value instanceof JsonObject valueObj) {
                for (var actionGroup : registry.getRegisteredGroups().entrySet()) {
                    for (var action : actionGroup.getValue().actions()) {
                        var actionName = action.id().toString();
                        if (valueObj.has(actionName) && valueObj.get(actionName) instanceof JsonObject actionValueObj) {
                            limitation.set(action, ActionLimitationValue.readFrom(action, actionValueObj));
                        }
                    }
                }
            }
        }
        if (object.has("stamina") && object.get("stamina") instanceof JsonPrimitive staminaValue && staminaValue.isString()) {
            var staminaID = ResourceLocation.tryParse(staminaValue.getAsString());
            if (staminaID != null && staminaRegistry.isRegistered(staminaID)) {
                limitation.setStaminaType(staminaID);
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
