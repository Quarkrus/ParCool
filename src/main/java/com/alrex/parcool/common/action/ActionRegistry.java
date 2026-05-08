package com.alrex.parcool.common.action;

import com.alrex.parcool.common.BasicRegistry;

import java.util.Map;

public class ActionRegistry extends BasicRegistry<String, ActionGroup> {
    public Map<String, ActionGroup> getRegisteredGroups() {
        return getRegistry();
    }

    public void register(ActionGroup group) {
        register(group.namespace(), group);
    }
}
