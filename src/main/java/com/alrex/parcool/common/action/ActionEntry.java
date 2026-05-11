package com.alrex.parcool.common.action;

import com.alrex.parcool.common.Parkourability;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

public class ActionEntry<T extends Action> implements Comparable<ActionEntry<?>> {
    private final short index;
    private final ResourceLocation id;
    private final Class<T> clazz;
    private final ActionConstructor<T> factory;
    private final StaminaConsumption defaultStaminaConsumption;
    private final @Nullable ActionEntry<? extends ContinuableAction> parent;
    private final ArrayList<ActionEntry<? extends Action>> children = new ArrayList<>();

    public ActionEntry(
            short index,
            ResourceLocation id,
            Class<T> clazz,
            ActionConstructor<T> factory,
            StaminaConsumption defaultStaminaConsumption,
            @Nullable ActionEntry<? extends ContinuableAction> parent
    ) {
        this.index = index;
        this.id = id;
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

    public ResourceLocation id() {
        return id;
    }

    public StaminaConsumption defaultStaminaConsumption() {
        return defaultStaminaConsumption;
    }

    @Nullable
    public ActionEntry<? extends ContinuableAction> parent() {
        return parent;
    }

    public Iterable<ActionEntry<? extends Action>> children() {
        return children;
    }

    public T createInstance(Parkourability parkourability) {
        return factory.construct(parkourability, this);
    }

    @Override
    public int compareTo(@Nonnull ActionEntry<?> o) {
        var strComp = this.id.getNamespace().compareTo(o.id.getNamespace());
        if (strComp != 0) return strComp;
        return Short.compare(this.index, o.index);
    }

    public interface ActionConstructor<T extends Action> {
        T construct(Parkourability parkourability, ActionEntry<T> entry);
    }
}
