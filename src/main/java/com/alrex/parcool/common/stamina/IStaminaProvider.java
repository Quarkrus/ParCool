package com.alrex.parcool.common.stamina;

import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public interface IStaminaProvider<T extends AbstractStamina> {
    T newInstance(Player owner, @Nullable AbstractStamina old);
}
