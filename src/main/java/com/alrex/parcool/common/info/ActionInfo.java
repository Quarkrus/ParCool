package com.alrex.parcool.common.info;

import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.nbt.Tag;

public class ActionInfo {
	private final Limitations[] Limitations = new Limitations[]{
			new Limitations(), //server limitation
			new Limitations()  //individual limitation
	};

	public Limitations getServerLimitation() {
		return Limitations[0];
	}

	public Limitations getIndividualLimitation() {
		return Limitations[1];
	}

	public ClientInformation getClientInformation() {
		return clientInformation;
	}

	private final ClientInformation clientInformation = new ClientInformation();

	public boolean can(Class<? extends Action> action) {
		for (Limitations limitation : Limitations) {
			if (!limitation.isPermitted(action)) return false;
		}
		return getClientInformation().get(ParCoolConfig.Client.Booleans.ParCoolIsActive)
				&& getClientInformation().getPossibilityOf(action);
	}

	public int getStaminaConsumptionOf(Class<? extends Action> action) {
		int value = getClientInformation().getStaminaConsumptionOf(action);
		for (Limitations limitation : Limitations) {
			if (limitation.isEnabled()) value = Math.max(value, limitation.getLeastStaminaConsumption(action));
		}
		return value;
	}

	public int getStaminaRecoveryLimit() {
		int value = Integer.MAX_VALUE;
		for (Limitations limitation : Limitations) {
			if (limitation.isEnabled())
				value = Math.min(value, limitation.get(ParCoolConfig.Server.Integers.MaxStaminaRecovery));
		}
		return value;
	}

	public int getMaxStaminaLimit() {
		//int value = getClientInformation().get(ParCoolConfig.Client.Integers.MaxStamina);
		int value = Integer.MAX_VALUE;
		for (Limitations limitation : Limitations) {
			if (limitation.isEnabled())
				value = Math.min(value, limitation.get(ParCoolConfig.Server.Integers.MaxStaminaLimit));
		}
		return value;
	}

	public boolean isStaminaInfinite(boolean creativeOrSpectator) {
		if (getClientInformation().get(ParCoolConfig.Client.Booleans.InfiniteStamina) && isInfiniteStaminaPermitted())
			return true;
		return creativeOrSpectator && getClientInformation().get(ParCoolConfig.Client.Booleans.InfiniteStaminaWhenCreative);
	}

	public boolean isInfiniteStaminaPermitted() {
		for (Limitations limitation : Limitations) {
			if (!limitation.isInfiniteStaminaPermitted()) return false;
		}
		return true;
	}

	public void readTag(Tag inbt) {
		getIndividualLimitation().readTag(inbt);
	}

	public Tag writeTag() {
		return getIndividualLimitation().writeTag();
	}
}
