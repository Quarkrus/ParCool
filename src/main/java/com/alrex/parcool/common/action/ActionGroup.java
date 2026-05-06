package com.alrex.parcool.common.action;

import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public record ActionGroup(String namespace, List<Entry<? extends Action>> actions) {
    public static class Builder {
        private final String namespace;
        private final LinkedList<Entry<? extends Action>> registered = new LinkedList<>();

        public String getNamespace() {
            return namespace;
        }

        public Builder(String namespace) {
            this.namespace = namespace;
        }

        public <T extends Action> Entry<T> add(String name, Class<T> clazz, Supplier<T> factory, StaminaConsumption consumption) {
            return add(name, clazz, factory, consumption, null);
        }

        public <T extends Action> Entry<T> add(String name, Class<T> clazz, Supplier<T> factory, StaminaConsumption consumption, Entry<?> parent) {
            var entry = new Entry<>(registered.size(), new ResourceLocation(namespace, name), clazz, factory, consumption, parent);
            registered.addLast(entry);
            return entry;
        }

        public ActionGroup build() {
            return new ActionGroup(namespace, registered.stream().toList());
        }
    }

    public record Entry<T extends Action>(
            int index,
            ResourceLocation name,
            Class<T> clazz,
            Supplier<T> factory,
            StaminaConsumption defaultStaminaConsumption,
            @Nullable Entry<? extends Action> parent
    ) {
        public T createInstance() {
            return factory.get();
        }
    }
}
