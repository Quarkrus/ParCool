package com.alrex.parcool.common.action;

import com.alrex.parcool.common.Parkourability;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;

import java.util.Collection;
import java.util.Collections;

public abstract class Action {
	public Action(Parkourability parkourability, ActionEntry<? extends Action> entry) {
		this.entry = entry;
		this.parkourability = parkourability;
	}

	protected final Parkourability parkourability;
	protected final ActionEntry<? extends Action> entry;
	private int tickFromStarted = -1;

	public ActionEntry<? extends Action> getEntry() {
		return entry;
	}

	private final Collection<ActionEntry<? extends ContinuableAction>> exclusiveActions = exclusiveActions();

	public SynchronizedDataHolder getSynchronizedData() {
		return SynchronizedDataHolder.empty();
	}

	public LogicalSide getTriggeredSide() {
		return LogicalSide.CLIENT;
	}

	protected Collection<ActionEntry<? extends ContinuableAction>> exclusiveActions() {
		return Collections.emptyList();
	}

	public void tick() {
		if (tickFromStarted >= 0) {
			tickFromStarted++;
		}
	}

	public void start() {
		tickFromStarted = 0;
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

	public boolean canStart() {
		var parent = entry.parent();
		if (parent != null && !parkourability.get(parent).isDoing()) {
			return false;
		}
		for (var exclusiveAction : exclusiveActions) {
			if (parkourability.get(exclusiveAction).isDoing()) {
				return false;
			}
		}
		return true;
	}

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
