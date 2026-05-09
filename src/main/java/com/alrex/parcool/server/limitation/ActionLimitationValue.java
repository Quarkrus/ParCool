package com.alrex.parcool.server.limitation;

import com.alrex.parcool.common.action.ActionEntry;
import com.alrex.parcool.common.action.StaminaConsumption;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;

public record ActionLimitationValue(boolean possible, StaminaConsumption staminaConsumption) {
    public static final ActionLimitationValue NO_LIMIT = new ActionLimitationValue(true, new StaminaConsumption((short) 0, (short) 0, (short) 0));

    public static ActionLimitationValue compile(ActionLimitationValue v1, ActionLimitationValue v2) {
        if (v1 == NO_LIMIT && v2 == NO_LIMIT) return NO_LIMIT;
        return new ActionLimitationValue(
                v1.possible && v2.possible,
                new StaminaConsumption(
                        (short) Math.max(v1.staminaConsumption.onStart(), v2.staminaConsumption.onStart()),
                        (short) Math.max(v1.staminaConsumption.onWorking(), v2.staminaConsumption.onWorking()),
                        (short) Math.max(v1.staminaConsumption.onFinish(), v2.staminaConsumption.onFinish())
                )
        );
    }

    public boolean isDefault(ActionEntry<?> actionEntry) {
        if (!possible) return false;
        return actionEntry.defaultStaminaConsumption().equals(staminaConsumption);
    }

    public void writeTo(FriendlyByteBuf packet) {
        packet.writeBoolean(possible);
        packet.writeShort(staminaConsumption.onStart());
        packet.writeShort(staminaConsumption.onWorking());
        packet.writeShort(staminaConsumption.onFinish());
    }

    public static ActionLimitationValue readFrom(FriendlyByteBuf packet) {
        return new ActionLimitationValue(packet.readBoolean(), new StaminaConsumption(packet.readShort(), packet.readShort(), packet.readShort()));
    }

    public JsonObject writeToJson(ActionEntry<?> action) {
        var obj = new JsonObject();
        obj.add("permit", new JsonPrimitive(possible));
        var staminaConsumptionObj = new JsonObject();
        staminaConsumptionObj.add("start", new JsonPrimitive(staminaConsumption.onStart()));
        staminaConsumptionObj.add("working", new JsonPrimitive(staminaConsumption.onWorking()));
        staminaConsumptionObj.add("finish", new JsonPrimitive(staminaConsumption.onFinish()));
        obj.add("cost", staminaConsumptionObj);
        return obj;
    }

    public static ActionLimitationValue readFrom(ActionEntry<?> action, JsonObject actionValueObj) {
        var possible = true;
        if (actionValueObj.has("permit") && actionValueObj.get("permit") instanceof JsonPrimitive permitValue && permitValue.isBoolean()) {
            possible = permitValue.getAsBoolean();
        }
        var staminaConsumption = action.defaultStaminaConsumption();
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
