package com.alrex.parcool.common.action;

import com.alrex.parcool.common.Parkourability;
import net.minecraft.world.entity.player.Player;

public class AdditionalProperties {
	public static class StatusTick {
		private int duration;
		private boolean doing;

		private void update(boolean doing) {
			if (this.doing == doing) {
				duration++;
			} else {
				duration = 0;
			}
			this.doing = doing;
		}

		public int durationDoing() {
			return doing ? duration : 0;
		}

		public int durationNotDoing() {
			return !doing ? duration : 0;
		}
	}
	private int tickAfterLastJump = 0;
	private final StatusTick sprint = new StatusTick();
	private final StatusTick sneak = new StatusTick();
	private final StatusTick onGround = new StatusTick();
	private final StatusTick flying = new StatusTick();
	private final StatusTick inWater = new StatusTick();

	public void onJump() {
		tickAfterLastJump = 0;
	}

	public void onTick(Player player, Parkourability parkourability) {
		tickAfterLastJump++;
		sprint.update(player.isSprinting());
		sneak.update(player.isShiftKeyDown());
		onGround.update(player.isOnGround());
		flying.update(player.getAbilities().flying);
		inWater.update(player.isInWater());
	}

	public StatusTick getSprintDurations() {
		return sprint;
	}

	public StatusTick getSneakDurations() {
		return sneak;
	}

	public StatusTick getOnGroundDurations() {
		return onGround;
	}

	public StatusTick getFlyingDurations() {
		return flying;
	}

	public StatusTick getInWaterDurations() {
		return inWater;
	}
}
