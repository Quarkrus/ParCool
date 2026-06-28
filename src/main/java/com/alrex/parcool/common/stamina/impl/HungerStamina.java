package com.alrex.parcool.common.stamina.impl;

import com.alrex.parcool.common.stamina.AbstractLocalStamina;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public class HungerStamina extends AbstractLocalStamina {
    public HungerStamina(Player owner, @Nullable AbstractLocalStamina __) {
        super(owner);
    }

    @Override
    public void setValue(int value) {
    }

    @Override
    public void consume(int value) {
        //TODO
        if (isInfinite()) return;
    }

    @Override
    public void recover(int value) {
    }

    @Override
    public int max() {
        return 20;
    }

    @Override
    public int value() {
        return owner.getFoodData().getFoodLevel();
    }

    @Override
    public boolean isExhausted() {
        return value() <= 3;
    }

    @Override
    public boolean imposePenalty() {
        return false;
    }
}
