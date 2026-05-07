package com.alrex.parcool.common.stamina;

import net.minecraft.resources.ResourceLocation;

import java.util.TreeMap;

public class StaminaTypeRegistry {
    private final TreeMap<ResourceLocation, Entry<? extends AbstractStamina>> map = new TreeMap<>();

    public record Entry<T extends AbstractStamina>(ResourceLocation id, String name, IStaminaProvider<T> provider) {
    }

    public <T extends AbstractStamina> Entry<T> register(ResourceLocation id, String name, IStaminaProvider<T> provider) {
        var entry = new Entry<>(id, name, provider);
        map.put(id, entry);
        return entry;
    }

    public IStaminaProvider<?> getProvider(ResourceLocation id) {
        return map.getOrDefault(id, )
    }
}
