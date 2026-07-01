package com.alrex.parcool.server.limitation;

import com.alrex.parcool.api.action.ActionEntry;
import com.alrex.parcool.api.action.StaminaConsumption;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;

public record ActionLimitationValue(boolean possible, StaminaConsumption cost) {
    public static final ActionLimitationValue NO_LIMIT = new ActionLimitationValue(true, new StaminaConsumption((short) 0, (short) 0, (short) 0));

    public static ActionLimitationValue compile(ActionLimitationValue v1, ActionLimitationValue v2) {
        if (v1 == NO_LIMIT && v2 == NO_LIMIT) return NO_LIMIT;
        return new ActionLimitationValue(
                v1.possible && v2.possible,
                new StaminaConsumption(
                        (short) Math.max(v1.cost.onStart(), v2.cost.onStart()),
                        (short) Math.max(v1.cost.onWorking(), v2.cost.onWorking()),
                        (short) Math.max(v1.cost.onFinish(), v2.cost.onFinish())
                )
        );
    }

    public boolean isDefault(ActionEntry<?> actionEntry) {
        if (!possible) return false;
        return actionEntry.option().defaultCost().equals(cost);
    }

    public void writeTo(FriendlyByteBuf packet) {
        packet.writeBoolean(possible);
        packet.writeShort(cost.onStart());
        packet.writeShort(cost.onWorking());
        packet.writeShort(cost.onFinish());
    }

    public static ActionLimitationValue readFrom(FriendlyByteBuf packet) {
        return new ActionLimitationValue(packet.readBoolean(), new StaminaConsumption(packet.readShort(), packet.readShort(), packet.readShort()));
    }

    public JsonObject writeToJson(ActionEntry<?> action) {
        var obj = new JsonObject();
        obj.add("permit", new JsonPrimitive(possible));
        var staminaConsumptionObj = new JsonObject();
        staminaConsumptionObj.add("start", new JsonPrimitive(cost.onStart()));
        staminaConsumptionObj.add("working", new JsonPrimitive(cost.onWorking()));
        staminaConsumptionObj.add("finish", new JsonPrimitive(cost.onFinish()));
        obj.add("cost", staminaConsumptionObj);
        return obj;
    }

    public static ActionLimitationValue readFrom(ActionEntry<?> action, JsonObject actionValueObj) {
        var possible = true;
        if (actionValueObj.has("permit") && actionValueObj.get("permit") instanceof JsonPrimitive permitValue && permitValue.isBoolean()) {
            possible = permitValue.getAsBoolean();
        }
        var staminaConsumption = action.option().defaultCost();
        var costOnStart = staminaConsumption.onStart();
        var costOnWorking = staminaConsumption.onWorking();
        var costOnFinish = staminaConsumption.onFinish();
        if (actionValueObj.has("cost") && actionValueObj.get("cost") instanceof JsonObject costValue) {
            if (costValue.has("start") && costValue.get("start") instanceof JsonPrimitive costStartValue && costStartValue.isNumber()) {
                costOnStart = (short) Mth.clamp(costStartValue.getAsShort(), 0, Short.MAX_VALUE);
            }
            if (costValue.has("working") && costValue.get("working") instanceof JsonPrimitive costStartValue && costStartValue.isNumber()) {
                costOnWorking = (short) Mth.clamp(costStartValue.getAsShort(), 0, Short.MAX_VALUE);
            }
            if (costValue.has("finish") && costValue.get("finish") instanceof JsonPrimitive costStartValue && costStartValue.isNumber()) {
                costOnFinish = (short) Mth.clamp(costStartValue.getAsShort(), 0, Short.MAX_VALUE);
            }
        }
        staminaConsumption = new StaminaConsumption(costOnStart, costOnWorking, costOnFinish);
        return new ActionLimitationValue(possible, staminaConsumption);
    }
}
