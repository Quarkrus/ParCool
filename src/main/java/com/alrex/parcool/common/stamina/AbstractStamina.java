package com.alrex.parcool.common.stamina;

import net.minecraft.world.entity.player.Player;

public abstract class AbstractStamina implements IReadonlyStamina {
    public AbstractStamina(Player owner) {
        this.owner = owner;
    }

    protected final Player owner;

    abstract void setValue(int value);

    abstract void consume(int value);

    abstract void recover(int value);

    void tick() {
    }

    boolean imposePenalty() {
        return isExhausted();
    }
}
