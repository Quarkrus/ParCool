package com.alrex.parcool.common.action;

import java.util.TreeMap;

public class ActionRegistry {
    private boolean frozen;
    private final TreeMap<String, ActionGroup> registeredGroups = new TreeMap<>();

    public TreeMap<String, ActionGroup> getRegisteredGroups() {
        return registeredGroups;
    }

    public void register(ActionGroup group) {
        if (frozen) {
            throw new IllegalStateException("This ActionRegistry is already frozen");
        }
        if (registeredGroups.containsKey(group.namespace())) {
            throw new IllegalStateException(String.format("ActionGroup[%s] is already registered", group.namespace()));
        }
        registeredGroups.put(group.namespace(), group);
    }

    public void freeze() {
        frozen = true;
    }
}
