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

	public ActionEntry<? extends Action> getEntry() {
		return entry;
	}

	private final Collection<ActionEntry<? extends Action>> exclusiveActions = exclusiveActions();

	private boolean doing = false;
	private int doingTick = 0;
	private int notDoingTick = 0;
	private int tickFromStarted = -1;

	public SynchronizedDataHolder getSynchronizedData() {
		return SynchronizedDataHolder.empty();
	}

	public LogicalSide getTriggeredSide() {
		return LogicalSide.CLIENT;
	}

	protected Collection<ActionEntry<? extends Action>> exclusiveActions() {
		return Collections.emptyList();
	}

	public boolean isJustStarted() {
		return isDoing() && getDoingTick() == 0;
	}

	public int getDoingTick() {
		return doingTick;
	}

	public int getNotDoingTick() {
		return notDoingTick;
	}

	public boolean isDoing() {
		return doing;
	}

	public void tick() {
		if (doing) {
			doingTick++;
			notDoingTick = 0;
		} else {
			notDoingTick++;
			doingTick = 0;
		}
		if (tickFromStarted >= 0) {
			tickFromStarted++;
		}
	}

	public void start() {
		if (doing) return;
		doing = true;
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

	public void finish() {
		if (!doing) return;
		for (var child : entry.children()) {
			parkourability.get(child).finish();
		}
		doing = false;
		if (parkourability.player().isLocalPlayer()) {
			onStopInLocalClient();
			onStopInClient();
		} else {
			if (parkourability.player().level.isClientSide()) {
				onStopInOtherClient();
				onStopInClient();
			} else {
				onStopInServer();
			}
		}
		onStop();
	}

	@OnlyIn(Dist.CLIENT)
	public boolean canStart() {
		var parent = entry.parent();
		if (parent != null && !parkourability.get(entry).isDoing()) {
			return false;
		}
		for (var exclusiveAction : exclusiveActions) {
			if (parkourability.get(exclusiveAction).isDoing()) {
				return false;
			}
		}
		return true;
	}

	@OnlyIn(Dist.CLIENT)
	public abstract boolean canContinue();

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

	public void onStop() {
	}

	public void onStopInServer() {
	}

	public void onStopInClient() {
	}

	public void onStopInOtherClient() {
	}

	public void onStopInLocalClient() {
	}

	public void onWorkingTick() {
	}

	public void onWorkingTickInServer() {
	}

	@OnlyIn(Dist.CLIENT)
	public void onWorkingTickInClient() {
	}

	@OnlyIn(Dist.CLIENT)
	public void onWorkingTickInOtherClient() {
	}

	@OnlyIn(Dist.CLIENT)
	public void onWorkingTickInLocalClient() {
	}

	public void onTick() {
	}

	public void onServerTick() {
	}

	@OnlyIn(Dist.CLIENT)
	public void onClientTick() {
	}

	@OnlyIn(Dist.CLIENT)
	public void onRenderTick() {
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
