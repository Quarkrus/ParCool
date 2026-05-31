package com.alrex.parcool.common;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.action.*;
import com.alrex.parcool.common.info.ActionInfo;
import com.alrex.parcool.common.info.CompiledLimitation;
import com.alrex.parcool.common.stamina.IReadonlyStamina;
import com.alrex.parcool.common.stamina.ReadonlyStamina;
import com.alrex.parcool.common.stamina.StaminaTypes;
import com.alrex.parcool.server.limitation.ILimitationEntry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collections;

public class Parkourability {
	public static Parkourability get(Player player) {
		if (player instanceof IParkourabilityHolder holder) {
			return holder.getParkourability();
		}
		return null;
	}

	private final ActionInfo info = new ActionInfo();
	private final AdditionalProperties properties = new AdditionalProperties();
	private final BehaviorEnforcer enforcer = new BehaviorEnforcer();
	private final ActionSet actions;
	private final Player player;
	private IReadonlyStamina stamina;

	public Parkourability(Player player, ActionRegistry registry) {
		this.player = player;
		this.actions = new ActionSet(this, registry);
		if (player.isLocalPlayer()) {
			this.info.setClientLimitation(CompiledLimitation.compile(Collections.singletonList(
                    ParCool.getLimitationRegistry().getGlobalLimitation()
			)));
            this.stamina = ParCool.getStaminaTypeRegistry().getRegistry()
                    .get(StaminaTypes.PARCOOL_STAMINA.id())
                    .provider()
                    .newInstance(player, null);
		} else {
			if (player instanceof ServerPlayer) {
				this.info.setServerLimitation(ParCool.getLimitationRegistry().getLimitationSet(player.getUUID()));
			}
			this.stamina = ReadonlyStamina.DEFAULT;
		}
	}

	public <T extends Action> T get(ActionEntry<T> entry) {
		return actions.get(entry);
	}

	public ActionSet getActions() {
		return actions;
	}

	public Player player() {
		return player;
	}

	public AdditionalProperties getAdditionalProperties() {
		return properties;
	}

	public BehaviorEnforcer getBehaviorEnforcer() {
		return enforcer;
	}

	public ActionInfo getActionInfo() {
		return info;
	}

	public CompiledLimitation getServerLimitation() {
		return info.getServerLimitation();
	}

	public IReadonlyStamina getStamina() {
		return stamina;
	}

	public void updateStaminaInRemote(ReadonlyStamina newStamina) {
		if (stamina instanceof ReadonlyStamina) {
			stamina = newStamina;
		}
	}

	public boolean can(ActionEntry<?> actionEntry) {
		return getActionInfo().getServerLimitation().get(actionEntry).possible() && getActionInfo().getClientLimitation().get(actionEntry).possible();
	}

	public void CopyFrom(Parkourability original) {
		getActionInfo().setClientLimitation(original.getActionInfo().getClientLimitation());
		getActionInfo().setServerLimitation(original.getActionInfo().getServerLimitation());
	}

	public boolean getLimitedValue(ILimitationEntry.Bool entry) {
		return entry.select(getActionInfo().getClientLimitation().get(entry), getActionInfo().getServerLimitation().get(entry));
	}

	public int getLimitedValue(ILimitationEntry.Int entry) {
		return entry.select(getActionInfo().getClientLimitation().get(entry), getActionInfo().getServerLimitation().get(entry));
	}

	public double getLimitedValue(ILimitationEntry.Real entry) {
		return entry.select(getActionInfo().getClientLimitation().get(entry), getActionInfo().getServerLimitation().get(entry));
	}

	@OnlyIn(Dist.CLIENT)
	public boolean limitationIsNotSynced() {
		return !getServerLimitation().isSynced();
	}
}
