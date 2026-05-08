package com.alrex.parcool.common.action;

import com.alrex.parcool.common.Parkourability;

import javax.annotation.Nonnull;
import java.util.*;

public class ActionSet implements Iterable<Action> {
    private final TreeMap<String, List<Action>> actions = new TreeMap<>();
    private final List<Action> iterationList;

    public ActionSet(Parkourability parkourability, ActionRegistry registry) {
        for (var group : registry.getRegisteredGroups().entrySet()) {
            actions.put(group.getKey(), group.getValue().actions().stream().map(it -> (Action) it.createInstance(parkourability)).toList());
        }
        var list = new LinkedList<Action>();
        Queue<ActionEntry<?>> queue = new LinkedList<>();
        for (var actionList : actions.values()) {
            for (var action : actionList) {
                if (action.getEntry().parent() == null) {
                    queue.add(action.getEntry());
                }
            }
        }
        while (!queue.isEmpty()) {
            var entry = queue.poll();
            var action = actions.get(entry.name().getNamespace()).get(entry.index());
            list.add(action);
            for (var child : entry.children()) {
                queue.add(child);
            }
        }
        iterationList = Collections.unmodifiableList(list);
    }

    public <T extends Action> T get(ActionEntry<T> entry) {
        return (T) actions.get(entry.name().getNamespace()).get(entry.index());
    }

    @Nonnull
    @Override
    public Iterator<Action> iterator() {
        return iterationList.iterator();
    }
}
