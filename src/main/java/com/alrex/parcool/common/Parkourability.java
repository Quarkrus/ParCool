package com.alrex.parcool.common;

import com.alrex.parcool.common.action.*;
import com.alrex.parcool.common.info.ActionInfo;
import com.alrex.parcool.common.info.ClientSetting;
import com.alrex.parcool.common.info.CompiledLimitation;
import com.alrex.parcool.common.network.SyncClientInformationMessage;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class Parkourability {
	@Nullable
	public static Parkourability get(Player player) {
		if (player instanceof IParkourabilityHolder holder) {
			return holder.getParkourability();
		}
		return null;
	}

	private final ActionInfo info;
	private final AdditionalProperties properties = new AdditionalProperties();
	private final BehaviorEnforcer enforcer = new BehaviorEnforcer();
	private final ActionSet actions;
	private final Player player;
	private int synchronizeTrialCount = 0;

	public Parkourability(Player player, ActionRegistry registry) {
		info = new ActionInfo();
		this.player = player;
		actions = new ActionSet(this, registry);
	}

	public <T extends Action> T get(ActionEntry<T> entry) {
		return actions.get(entry);
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

	public ClientSetting getClientInfo() {
		return info.getClientSetting();
	}

	public CompiledLimitation getServerLimitation() {
		return info.getServerLimitation();
	}

	public void CopyFrom(Parkourability original) {
		getActionInfo().setClientSetting(original.getActionInfo().getClientSetting());
		getActionInfo().setServerLimitation(original.getActionInfo().getServerLimitation());
	}

	public boolean isDoingNothing() {
		return actions.stream().noneMatch(Action::isDoing);
	}

	public boolean getLimitedValue(ParCoolConfig.Client.Booleans client, ParCoolConfig.Server.Booleans server) {
		if (server.AdvantageousValue) {
			return (getClientInfo().get(client) && getServerLimitation().get(server));
		} else {
			return !(getClientInfo().get(client) || getServerLimitation().get(server));
		}
	}

	public int getLimitedValue(ParCoolConfig.Client.Integers client, ParCoolConfig.Server.Integers server) {
		if (server.Advantageous == ParCoolConfig.AdvantageousDirection.Higher) {
			return Math.min(getClientInfo().get(client), getServerLimitation().get(server));
		} else {
			return Math.max(getClientInfo().get(client), getServerLimitation().get(server));
		}
	}

	public double getLimitedValue(ParCoolConfig.Client.Doubles client, ParCoolConfig.Server.Doubles server) {
		if (server.Advantageous == ParCoolConfig.AdvantageousDirection.Higher) {
			return Math.min(getClientInfo().get(client), getServerLimitation().get(server));
		} else {
			return Math.max(getClientInfo().get(client), getServerLimitation().get(server));
		}
	}

	@OnlyIn(Dist.CLIENT)
	public void trySyncLimitation(LocalPlayer player) {
		synchronizeTrialCount++;
		SyncClientInformationMessage.sync(player, true);
	}

	@OnlyIn(Dist.CLIENT)
	public int getSynchronizeTrialCount() {
		return synchronizeTrialCount;
	}

	public void incrementSynchronizeTrialCount() {
		synchronizeTrialCount++;
	}

	@OnlyIn(Dist.CLIENT)
	public boolean limitationIsNotSynced() {
		return !getServerLimitation().isSynced();
	}

	@SafeVarargs
	public final Boolean isDoingAny(Class<? extends Action>... actions) {
		for (Class<? extends Action> action : actions) {
			if (get(action).isDoing()) {
				return true;
			}
		}

		return false;
	}
}
