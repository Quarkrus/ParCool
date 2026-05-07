package com.alrex.parcool.common.stamina;

import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public class ParCoolStamina extends AbstractStamina {
    private final int max;
    private int value = 2000;
    private boolean exhausted = false;
    private int recoverCooldown = 0;

    public ParCoolStamina(Player owner, @Nullable AbstractStamina old, int max) {
        super(owner);
        this.max = max;
        if (old != null) {
            setValue((int) (max * old.getValue() / (double) old.getMax()));
            exhausted = old.isExhausted();
        }
    }

    @Override
    public int getMax() {
        return max;
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public boolean isExhausted() {
        return exhausted;
    }

    @Override
    public void setValue(int value) {
        this.value = value;
        if (this.value < 0) this.value = 0;
        if (this.value > max) this.value = max;
    }

    @Override
    public void consume(int value) {
        if (exhausted) return;
        this.value -= value;
        if (this.value <= 0) {
            this.value = 0;
            exhausted = true;
        }
        recoverCooldown = 30;
    }

    @Override
    public void recover(int value) {
        this.value += value;
        if (this.value > max) {
            this.value = max;
            exhausted = false;
        }
    }

    @Override
    public void tick() {
        recoverCooldown--;
        if (recoverCooldown <= 0) {
            //TODO
            recover(owner.isOnGround() ? 5 : 2);
        }
    }
}
