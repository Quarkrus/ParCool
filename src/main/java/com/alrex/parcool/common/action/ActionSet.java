package com.alrex.parcool.common.action;

import com.alrex.parcool.common.Parkourability;

import java.util.List;
import java.util.TreeMap;

public class ActionSet {
    private final TreeMap<String, List<Action>> actions = new TreeMap<>();

    public ActionSet(Parkourability parkourability, ActionRegistry registry) {
        for (var group : registry.getRegisteredGroups().entrySet()) {
            actions.put(group.getKey(), group.getValue().actions().stream().map(it -> (Action) it.createInstance(parkourability)).toList());
        }
    }

    public <T extends Action> T get(ActionEntry<T> entry) {
        return (T) actions.get(entry.name().getNamespace()).get(entry.index());
    }
}
