package com.alrex.parcool.common.action;

import com.alrex.parcool.common.BasicRegistry;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.TreeMap;

public class ActionRegistry extends BasicRegistry<String, ActionGroup> {
    private Map<ResourceLocation, ActionEntry<?>> registeredEntries = new TreeMap<>();
    public Map<String, ActionGroup> getRegisteredGroups() {
        return getRegistry();
    }

    public Map<ResourceLocation, ActionEntry<?>> getRegisteredActions() {
        return registeredEntries;
    }

    @Nullable
    public ActionEntry<?> get(ResourceLocation id) {
        var group = getRegisteredGroups().get(id.getNamespace());
        if (group == null) return null;
        for (var actionEntry : group.actions()) {
            if (actionEntry.id().equals(id)) return actionEntry;
        }
        return null;
    }

    public void register(ActionGroup group) {
        register(group.namespace(), group);
        for (var entry : group.actions()) {
            registeredEntries.put(entry.id(), entry);
        }
    }
}
