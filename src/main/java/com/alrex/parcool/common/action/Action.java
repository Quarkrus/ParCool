package com.alrex.parcool.common.action;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.network.ActionStatePacket;
import com.alrex.parcool.common.network.ActionStateSetPacket;
import com.alrex.parcool.common.stamina.AbstractLocalStamina;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;

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
	private int tickSinceStarted = -1;

	public int getTickSinceStarted() {
		return tickSinceStarted;
	}

	public ActionEntry<? extends Action> getEntry() {
		return entry;
	}

	public SynchronizedDataHolder getSynchronizedData() {
		return SynchronizedDataHolder.empty();
	}

	public void tick() {
		if (tickSinceStarted >= 0) {
			tickSinceStarted++;
		}
	}

	public void start() {
		tickSinceStarted = 0;

		onStart();
		if (parkourability.player().isLocalPlayer()) {
			onStartInClient();
			onStartInLocalClient();
			if (parkourability.getStamina() instanceof AbstractLocalStamina stamina) {
				stamina.consume(parkourability.getCost(entry, StaminaConsumeType.START));
			}
		} else {
			if (parkourability.player().level.isClientSide()) {
				onStartInClient();
				onStartInOtherClient();
			} else {
				onStartInServer();
			}
		}
	}

	/// <strong>Warning</strong> : This method use internal system directly, without waiting system lifecycle.
	/// Therefore, the synchronization packet is not bundled with other packet, this cause increase of number of sent packet.
	/// And this can cause problems if used without being careful.
	///
	/// Please use <code>canStart</code> method as long as possible
	public void startExplicitly() {
		start();
		var packet = new ActionStateSetPacket(parkourability.player().getUUID());
		packet.add(new ActionStatePacket(
				entry.id().getNamespace(),
				Collections.singletonList(getSynchronizedData().packToEntry(ActionStatePacket.Type.START, entry))
		));
		ParCool.CONNECTION.send(PacketDistributor.ALL.noArg(), packet);
	}

	protected final boolean isPossible() {
		var player = parkourability.player();
		if (parkourability.player().isSpectator() //TODO
				|| (!entry.option().availableInFluid() && player.isInFluidType())
				|| (entry.option().needOnGround() && !player.isOnGround())
				|| (entry.option().neededPose() != null && entry.option().neededPose() != player.getPose())
				|| !parkourability.permit(entry)
		) {
			return false;
		}
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
		return !MinecraftForge.EVENT_BUS.post(new ParCoolActionEvent.TryToStart(parkourability.player(), this));
	}

	public final boolean isReadyToStart() {
		if (!isPossible()) return false;
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
	public void onLocalClientTick() {
	}

	@OnlyIn(Dist.CLIENT)
	public void onOtherClientTick() {
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
