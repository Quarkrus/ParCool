package com.alrex.parcool.common.action;

import com.alrex.parcool.common.Parkourability;
import net.minecraft.util.Tuple;

import javax.annotation.Nonnull;
import java.util.*;

public class ActionSet implements Iterable<Action> {
    private final TreeMap<String, List<Action>> actions = new TreeMap<>();
    private final List<Action> iterationList;
    private final List<? extends ActionExtension.Handler<? extends ActionExtension>> extHandlers;

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
        var extListenerLists = new ArrayList<Tuple<Class<? extends ActionExtension>, LinkedList<ActionExtension>>>();
        for (var extType : ActionExtension.EXTENSIONS) {
            extListenerLists.add(new Tuple<>(extType, new LinkedList<>()));
        }
        while (!queue.isEmpty()) {
            var entry = queue.poll();
            var action = actions.get(entry.id().getNamespace()).get(entry.index());
            list.add(action);
            for (var extListenerList : extListenerLists) {
                if (extListenerList.getA().isAssignableFrom(action.getClass())) {
                    extListenerList.getB().add((ActionExtension) action);
                }
            }
            for (var child : entry.children()) {
                queue.add(child);
            }
        }
        extHandlers = extListenerLists.stream().map(it -> createHandler(it.getA(), it.getB())).toList();
        iterationList = Collections.unmodifiableList(list);
    }

    private <T extends ActionExtension> ActionExtension.Handler<T> createHandler(Class<T> clazz, LinkedList<?> list) {
        return new ActionExtension.Handler<>(clazz, list.stream().map(clazz::cast).toList());
    }

    public <T extends Action> T get(ActionEntry<T> entry) {
        return (T) actions.get(entry.id().getNamespace()).get(entry.index());
    }

    public <T extends ActionExtension> Iterable<T> getExtensionListeners(Class<T> clazz) {
        for (var extHandler : extHandlers) {
            if (extHandler.match(clazz)) {
                return (List<T>) extHandler.getListeners();
            }
        }
        throw new IllegalStateException(String.format("Extension class [%s] is not registered", clazz));
    }

    @Nonnull
    @Override
    public Iterator<Action> iterator() {
        return iterationList.iterator();
    }
}
