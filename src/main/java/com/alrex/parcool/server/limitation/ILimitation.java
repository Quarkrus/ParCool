package com.alrex.parcool.server.limitation;

import com.alrex.parcool.api.action.ActionEntry;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Map;

public interface ILimitation {
    boolean get(ILimitationEntry.Bool entry);

    short get(ILimitationEntry.Int entry);

    float get(ILimitationEntry.Real entry);

    @Nullable
    ActionLimitationValue get(ActionEntry<?> entry);

    Map<ActionEntry<?>, ActionLimitationValue> actions();

    @Nullable
    default ResourceLocation getStaminaType() {
        return null;
    }
}
