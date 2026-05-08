package com.alrex.parcool.common.stamina;

import net.minecraft.world.entity.player.Player;

public abstract class AbstractLocalStamina implements IReadonlyStamina {
    public AbstractLocalStamina(Player owner) {
        this.owner = owner;
    }

    protected final Player owner;

    public abstract void setValue(int value);

    public abstract void consume(int value);

    public abstract void recover(int value);

    public void tick() {
    }

    public boolean imposePenalty() {
        return isExhausted();
    }
}
