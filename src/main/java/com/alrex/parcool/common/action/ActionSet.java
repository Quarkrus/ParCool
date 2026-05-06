package com.alrex.parcool.common.action;

import java.util.List;
import java.util.TreeMap;

public class ActionSet {
    private final TreeMap<String, List<Action>> actions = new TreeMap<>();

    public ActionSet(ActionRegistry registry) {
        for (var group : registry.getRegisteredGroups().entrySet()) {
            actions.put(group.getKey(), group.getValue().actions().stream().map(it -> (Action) it.createInstance()).toList());
        }
    }

    public <T extends Action> T get(ActionGroup.Entry<T> entry) {
        return (T) actions.get(entry.name().getNamespace()).get(entry.index());
    }
}
