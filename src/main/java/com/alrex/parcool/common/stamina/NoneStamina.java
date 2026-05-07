package com.alrex.parcool.common.stamina;

import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public class NoneStamina extends AbstractStamina {
    public NoneStamina(Player owner, @Nullable AbstractStamina __) {
        super(owner);
    }

    @Override
    public int getMax() {
        return 1;
    }

    @Override
    public int getValue() {
        return 1;
    }

    @Override
    public boolean isExhausted() {
        return false;
    }

    @Override
    public void setValue(int value) {
    }

    @Override
    public void consume(int value) {
    }

    @Override
    public void recover(int value) {
    }

    @Override
    public boolean imposePenalty() {
        return false;
    }
}
