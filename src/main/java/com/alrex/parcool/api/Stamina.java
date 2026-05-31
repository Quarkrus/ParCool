package com.alrex.parcool.api;

import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.stamina.AbstractLocalStamina;
import com.alrex.parcool.common.stamina.IReadonlyStamina;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class Stamina {
	@Nullable
	public static Stamina get(Player player) {
        var instance = Parkourability.get(player).getStamina();
		if (instance == null) {
			return null;
		}
		return new Stamina(instance);
	}

    private final IReadonlyStamina staminaInstance;

    private Stamina(IReadonlyStamina staminaInstance) {
		this.staminaInstance = staminaInstance;
	}

	public int getMaxValue() {
        return staminaInstance.max();
	}

	public int getValue() {
        return staminaInstance.value();
	}

	public boolean isExhausted() {
		return staminaInstance.isExhausted();
	}

	@OnlyIn(Dist.CLIENT)
	public void setValue(int value) {
        if (staminaInstance instanceof AbstractLocalStamina localStamina) {
            if (value < 0) {
                value = 0;
            } else if (value > getMaxValue()) {
                value = getMaxValue();
            }
            localStamina.setValue(value);
        }
	}

	@OnlyIn(Dist.CLIENT)
	public void consume(int value) {
        if (staminaInstance instanceof AbstractLocalStamina localStamina) {
            localStamina.consume(value);
        }
	}

	@OnlyIn(Dist.CLIENT)
	public void recover(int value) {
        if (staminaInstance instanceof AbstractLocalStamina localStamina) {
            localStamina.recover(value);
        }
	}
}
