package com.alrex.parcool.common;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.action.*;
import com.alrex.parcool.common.info.ActionInfo;
import com.alrex.parcool.common.info.CompiledLimitation;
import com.alrex.parcool.common.stamina.IReadonlyStamina;
import com.alrex.parcool.common.stamina.ReadonlyStamina;
import com.alrex.parcool.common.stamina.StaminaTypes;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.server.limitation.ILimitationEntry;
import com.alrex.parcool.server.limitation.Limitation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;
import java.util.TreeMap;

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
	private final TreeMap<ActionEntry<?>, Object> requestedContexts = new TreeMap<>();
	private final Player player;
	private IReadonlyStamina stamina;

	public Parkourability(Player player, ActionRegistry registry) {
		this.player = player;
		this.actions = new ActionSet(this, registry);
		if (player.isLocalPlayer()) {
			this.info.setClientLimitation(CompiledLimitation.compile(
					Limitation.readFromConfig(
							ParCoolConfig.getClientConfigLimitation(),
							ParCool.getActionRegistry(),
							ParCool.getStaminaTypeRegistry()
					)
			));
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

	public boolean permit(ActionEntry<?> actionEntry) {
		return getActionInfo().getServerLimitation().get(actionEntry).possible() && getActionInfo().getClientLimitation().get(actionEntry).possible();
	}

	/// Request the action start.
	/// All requests are cleared on tick finishing, so this must be called before the action is ticked
	public <Context, RequestableAction extends Action & IRequestable<Context>> void request(ActionEntry<RequestableAction> actionEntry, @Nonnull Context context) {
		requestedContexts.put(actionEntry, context);
	}

	public <Context, RequestableAction extends Action & IRequestable<Context>> Context getRequest(ActionEntry<RequestableAction> actionEntry) {
		return (Context) requestedContexts.remove(actionEntry);
	}

	public boolean canStartByRequest(IRequestable<?> requestable) {
		if (!(requestable instanceof Action action)) return false;
		var context = requestedContexts.get(action.getEntry());
		return context != null ? requestable.requestCanStart(context) : action.canStart();
	}

	public void finishTicking() {
		requestedContexts.clear();
	}

	public void copyFrom(Parkourability original) {
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

	public int getCost(ActionEntry<?> entry, StaminaConsumeType type) {
		return Math.max(
				getActionInfo().getClientLimitation().get(entry).cost().get(type),
				getActionInfo().getServerLimitation().get(entry).cost().get(type)
		);
	}
}
