package com.alrex.parcool.common.action;

import javax.annotation.Nullable;
import java.util.TreeMap;

public class ActionRegistry {
    @Nullable
    private static ActionRegistry instance = null;

    public static ActionRegistry getInstance() {
        if (instance == null) instance = new ActionRegistry();
        return instance;
    }

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
