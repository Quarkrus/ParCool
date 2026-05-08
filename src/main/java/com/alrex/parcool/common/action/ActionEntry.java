package com.alrex.parcool.common.action;

import com.alrex.parcool.common.Parkourability;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class ActionEntry<T extends Action> {
    private final short index;
    private final ResourceLocation name;
    private final Class<T> clazz;
    private final ActionConstructor<T> factory;
    private final StaminaConsumption defaultStaminaConsumption;
    private final @Nullable ActionEntry<? extends Action> parent;
    private final ArrayList<ActionEntry<? extends Action>> children = new ArrayList<>();

    public ActionEntry(
            short index,
            ResourceLocation name,
            Class<T> clazz,
            ActionConstructor<T> factory,
            StaminaConsumption defaultStaminaConsumption,
            @Nullable ActionEntry<? extends Action> parent
    ) {
        this.index = index;
        this.name = name;
        this.clazz = clazz;
        this.factory = factory;
        this.defaultStaminaConsumption = defaultStaminaConsumption;
        this.parent = parent;
        if (parent != null) {
            parent.children.add(this);
        }
    }

    public short index() {
        return index;
    }

    public ResourceLocation name() {
        return name;
    }

    public StaminaConsumption defaultStaminaConsumption() {
        return defaultStaminaConsumption;
    }

    @Nullable
    public ActionEntry<? extends Action> parent() {
        return parent;
    }

    public Iterable<ActionEntry<? extends Action>> children() {
        return children;
    }

    public T createInstance(Parkourability parkourability) {
        return factory.construct(parkourability, this);
    }

    public interface ActionConstructor<T extends Action> {
        T construct(Parkourability parkourability, ActionEntry<T> entry);
    }
}
