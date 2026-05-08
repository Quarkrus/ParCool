package com.alrex.parcool.common.stamina;

import com.alrex.parcool.common.BasicRegistry;
import net.minecraft.resources.ResourceLocation;

public class StaminaTypeRegistry extends BasicRegistry<ResourceLocation, StaminaTypeEntry<? extends AbstractLocalStamina>> {
    public <T extends AbstractLocalStamina> void register(StaminaTypeEntry<T> entry) {
        register(entry.id(), entry);
    }

    public IStaminaProvider<?> getProvider(ResourceLocation id) {
        return getRegistry().getOrDefault(id, StaminaTypes.PARCOOL_STAMINA).provider();
    }
}
