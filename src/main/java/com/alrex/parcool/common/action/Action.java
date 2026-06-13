package com.alrex.parcool.common.action;

import com.alrex.parcool.common.Parkourability;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Collection;

public abstract class Action {
	public Action(Parkourability parkourability, ActionEntry<? extends Action> entry) {
		this.entry = entry;
		this.parkourability = parkourability;
		exclusiveActions = null;
	}

	public Action(Parkourability parkourability, ActionEntry<? extends Action> entry, Collection<ActionEntry<? extends ContinuableAction>> exclusiveActions) {
		this.entry = entry;
		this.parkourability = parkourability;
		this.exclusiveActions = exclusiveActions;
	}

	protected final Parkourability parkourability;
	protected final ActionEntry<? extends Action> entry;
	@Nullable
	protected final Collection<ActionEntry<? extends ContinuableAction>> exclusiveActions;
	private int tickFromStarted = -1;

	public ActionEntry<? extends Action> getEntry() {
		return entry;
	}

	public SynchronizedDataHolder getSynchronizedData() {
		return SynchronizedDataHolder.empty();
	}

	public void tick() {
		if (tickFromStarted >= 0) {
			tickFromStarted++;
		}
	}

	public void start() {
		tickFromStarted = 0;
		onStart();
		if (parkourability.player().isLocalPlayer()) {
			onStartInClient();
			onStartInLocalClient();
		} else {
			if (parkourability.player().level.isClientSide()) {
				onStartInClient();
				onStartInOtherClient();
			} else {
				onStartInServer();
			}
		}
	}

	public final boolean isAbleToStart() {
		if (entry.option().needParentWorking()) {
			var parent = entry.parent();
			if (parent != null && !parkourability.get(parent).isDoing()) {
				return false;
			}
		}
		if (exclusiveActions != null) {
			for (var exclusiveAction : exclusiveActions) {
				if (parkourability.get(exclusiveAction).isDoing()) {
					return false;
				}
			}
		}
		return (this instanceof IRequestable<?> requestable)
				? parkourability.canStartByRequest(requestable)
				: canStart();
	}


	public abstract boolean canStart();

	public void onStart() {
	}

	public void onStartInServer() {
	}

	@OnlyIn(Dist.CLIENT)
	public void onStartInClient() {
	}

	@OnlyIn(Dist.CLIENT)
	public void onStartInOtherClient() {
	}

	@OnlyIn(Dist.CLIENT)
	public void onStartInLocalClient() {
	}

	public void onTick() {
	}

	public void onServerTick() {
	}

	@OnlyIn(Dist.CLIENT)
	public void onClientTick() {
	}

	@OnlyIn(Dist.CLIENT)
	public boolean wantsToShowStatusBar() {
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	public float getStatusValue() {
		return 0;
	}
}
