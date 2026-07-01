package com.alrex.parcool.common.stamina.impl;

import com.alrex.parcool.api.stamina.AbstractLocalStamina;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public class NoneStamina extends AbstractLocalStamina {
    public NoneStamina(Player owner, @Nullable AbstractLocalStamina __) {
        super(owner);
    }

    @Override
    public int max() {
        return 1;
    }

    @Override
    public int value() {
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

    @Override
    public boolean isInfinite() {
        return false;
    }
}
