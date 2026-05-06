package com.alrex.parcool.config;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.AnimatorList;
import com.alrex.parcool.client.gui.ColorTheme;
import com.alrex.parcool.client.hud.Position;
import com.alrex.parcool.client.hud.impl.HUDType;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.ActionGroup;
import com.alrex.parcool.common.action.impl.*;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.server.limitation.ILimitationEntry;
import com.alrex.parcool.server.limitation.LimitationEntries;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nullable;
import java.util.List;

public class ParCoolConfig {
	public enum ConfigGroup {
		Animation, CameraAnimation, HUD, Modifier, Control, Stamina, Other
	}

	public static class Client {
		public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
		public static final ForgeConfigSpec BUILT_CONFIG;

		public static ForgeConfigSpec.BooleanValue getPossibilityOf(Class<? extends Action> action) {
			return actionPossibilities[ActionGroup.getIndexOf(action)];
		}

		public static ForgeConfigSpec.BooleanValue canAnimate(Class<? extends Animator> animator) {
			return animatorPossibilities[AnimatorList.getIndex(animator)];
		}

		public static ForgeConfigSpec.IntValue getStaminaConsumptionOf(Class<? extends Action> action) {
			return staminaConsumptions[ActionGroup.getIndexOf(action)];
		}

		private static final ForgeConfigSpec.BooleanValue[] actionPossibilities = new ForgeConfigSpec.BooleanValue[ActionGroup.ACTIONS.size()];
		private static final ForgeConfigSpec.BooleanValue[] animatorPossibilities = new ForgeConfigSpec.BooleanValue[AnimatorList.ANIMATORS.size()];
		private static final ForgeConfigSpec.IntValue[] staminaConsumptions = new ForgeConfigSpec.IntValue[ActionGroup.ACTIONS.size()];
		public static final ForgeConfigSpec.EnumValue<HUDType> STAMINA_HUD_TYPE;
		public static final ForgeConfigSpec.EnumValue<Vault.TypeSelectionMode> VAULT_ANIMATION_MODE;
		public static final ForgeConfigSpec.EnumValue<Position.Horizontal> STAMINA_HUD_ALIGN_HORIZONTAL;
		public static final ForgeConfigSpec.EnumValue<Position.Vertical> STAMINA_HUD_ALIGN_VERTICAL;
		public static final ForgeConfigSpec.EnumValue<ColorTheme> GUI_COLOR_THEME;
		public static final ForgeConfigSpec.EnumValue<FastRun.ControlType> FAST_RUN_CONTROL;
		public static final ForgeConfigSpec.EnumValue<Crawl.ControlType> CRAWL_CONTROL;
		public static final ForgeConfigSpec.EnumValue<Flipping.ControlType> FLIP_CONTROL;
		public static final ForgeConfigSpec.EnumValue<HorizontalWallRun.ControlType> H_WALL_RUN_CONTROL;
		public static final ForgeConfigSpec.EnumValue<WallJump.ControlType> WALL_JUMP_CONTROL;
		public static final ForgeConfigSpec.EnumValue<ClingToCliff.ControlType> CLING_TO_CLIFF_CONTROL;
		public static final ForgeConfigSpec.EnumValue<IStamina.Type> STAMINA_TYPE;
		public static final ForgeConfigSpec.BooleanValue ENABLE_ANIMATION;
		public static final ForgeConfigSpec.BooleanValue ENABLE_FALLING_ANIMATION;
		public static final ForgeConfigSpec.BooleanValue ENABLE_LEAN_ANIMATION_OF_FAST_RUN;
		public static final ForgeConfigSpec.BooleanValue ENABLE_FPV_ANIMATION;
		public static final ForgeConfigSpec.BooleanValue ENABLE_CAMERA_ANIMATION_OF_DODGE;
		public static final ForgeConfigSpec.BooleanValue ENABLE_CAMERA_ANIMATION_OF_BACK_WALL_JUMP;
		public static final ForgeConfigSpec.BooleanValue ENABLE_CAMERA_ANIMATION_OF_ROLLING;
		public static final ForgeConfigSpec.BooleanValue ENABLE_CAMERA_ANIMATION_OF_FLIPPING;
		public static final ForgeConfigSpec.BooleanValue ENABLE_CAMERA_ANIMATION_OF_VAULT;
		public static final ForgeConfigSpec.BooleanValue ENABLE_CAMERA_ANIMATION_OF_H_WALL_RUN;
		public static final ForgeConfigSpec.BooleanValue ENABLE_CAMERA_ANIMATION_OF_HANG_DOWN;
		public static final ForgeConfigSpec.BooleanValue STAMINA_HUD_HIDE_WHEN_STAMINA_IS_INFINITE;
		public static final ForgeConfigSpec.BooleanValue SHOW_ACTION_STATUS_BAR;
		public static final ForgeConfigSpec.BooleanValue STAMINA_HUD_SHOW_ALWAYS;
		public static final ForgeConfigSpec.BooleanValue ENABLE_DOUBLE_TAPPING_FOR_DODGE;
		public static final ForgeConfigSpec.BooleanValue ENABLE_CRAWL_IN_AIR;
		public static final ForgeConfigSpec.BooleanValue ENABLE_VAULT_IN_AIR;
		public static final ForgeConfigSpec.BooleanValue CAN_GET_OFF_STEPS_WHILE_DODGE;
		public static final ForgeConfigSpec.BooleanValue ENABLE_ACTION_SOUNDS;
		public static final ForgeConfigSpec.BooleanValue ENABLE_ACTION_PARTICLES;
		public static final ForgeConfigSpec.BooleanValue ENABLE_3D_RENDERING_FOR_ZIPLINE;
		public static final ForgeConfigSpec.BooleanValue VAULT_NEED_KEY_PRESSED;
		public static final ForgeConfigSpec.BooleanValue HIDE_IN_BLOCK_NEED_SNEAK;
		public static final ForgeConfigSpec.BooleanValue SUBSTITUTE_SPRINT_FOR_FAST_RUN;
		public static final ForgeConfigSpec.BooleanValue SHOW_AUTO_RESYNCHRONIZATION_NOTIFICATION;
		public static final ForgeConfigSpec.BooleanValue PARCOOL_IS_ACTIVE;

		public static final ForgeConfigSpec.IntValue ACCEPTABLE_ANGLE_OF_WALL_JUMP;
		public static final ForgeConfigSpec.IntValue STAMINA_HUD_HORIZONTAL_OFFSET;
		public static final ForgeConfigSpec.IntValue STAMINA_HUD_VERTICAL_OFFSET;

		static {
			ForgeConfigSpec.Builder builder = BUILDER;
            builder.push("Possibility_of_Actions(Some_do_not_have_to_work)");
			{
				for (int i = 0; i < ActionGroup.ACTIONS.size(); i++) {
					actionPossibilities[i] = builder.define("can_" + ActionGroup.ACTIONS.get(i).getSimpleName(), true);
				}
			}
			builder.pop();
			builder.push("HUD");
			{
				STAMINA_HUD_TYPE = builder.defineEnum("stamina_hud_type", HUDType.Light);
				STAMINA_HUD_ALIGN_HORIZONTAL = builder.comment("horizontal alignment").defineEnum("hud_align_h_s", Position.Horizontal.Right);
				STAMINA_HUD_ALIGN_VERTICAL = builder.comment("vertical alignment").defineEnum("hud_align_v_s", Position.Vertical.Bottom);
				STAMINA_HUD_HORIZONTAL_OFFSET = builder.defineInRange("hud_offset_h", 0, -100, 100);
				STAMINA_HUD_VERTICAL_OFFSET = builder.defineInRange("hud_offset_v", 0, -100, 100);
				STAMINA_HUD_SHOW_ALWAYS = builder.define("show_stamina_hud_always", true);
				STAMINA_HUD_HIDE_WHEN_STAMINA_IS_INFINITE = builder.define("hide_stamina_hud_infinite_stamina", true);
				SHOW_ACTION_STATUS_BAR = builder.comment("HUD shows cooldown time, etc").define("show_action_status", true);
			}
			builder.pop();
			builder.push("Animations");
			{
				builder.push("Animators");
				{
					for (int i = 0; i < AnimatorList.ANIMATORS.size(); i++) {
						animatorPossibilities[i] = builder.define("enable_" + AnimatorList.ANIMATORS.get(i).getSimpleName(), true);
					}
				}
				builder.pop();
				ENABLE_ANIMATION = builder.define("enable_animation", true);
				ENABLE_FALLING_ANIMATION = builder.define("enable_falling_animation", true);
				ENABLE_FPV_ANIMATION = builder.define("enable_fpv_animation", true);
				ENABLE_LEAN_ANIMATION_OF_FAST_RUN = builder.define("enable_lean_animation_fast_run", true);
				ENABLE_ACTION_PARTICLES = builder.define("enable_particles", true);
				ENABLE_CAMERA_ANIMATION_OF_BACK_WALL_JUMP = builder.define("enable_camera_animation_backward_wall_jump", false);
				ENABLE_CAMERA_ANIMATION_OF_DODGE = builder.define("enable_camera_animation_dodge", false);
				ENABLE_CAMERA_ANIMATION_OF_FLIPPING = builder.define("enable_camera_animation_flipping", false);
				ENABLE_CAMERA_ANIMATION_OF_ROLLING = builder.define("enable_camera_animation_roll", true);
				ENABLE_CAMERA_ANIMATION_OF_HANG_DOWN = builder.define("enable_camera_animation_hang_down", true);
				ENABLE_CAMERA_ANIMATION_OF_VAULT = builder.define("enable_camera_animation_vault", true);
				ENABLE_CAMERA_ANIMATION_OF_H_WALL_RUN = builder.define("enable_camera_animation_h_wallrun", false);
			}
			builder.pop();
			builder.push("Control");
			{
				FAST_RUN_CONTROL = builder.defineEnum("fast-run_control", FastRun.ControlType.PressKey);
				CRAWL_CONTROL = builder.defineEnum("crawl_control", Crawl.ControlType.PressKey);
				FLIP_CONTROL = builder.defineEnum("flip_control", Flipping.ControlType.TapMovementAndJump);
				H_WALL_RUN_CONTROL = builder.defineEnum("h-wall-run_control", HorizontalWallRun.ControlType.PressKey);
				WALL_JUMP_CONTROL = builder.defineEnum("wall-jump_control", WallJump.ControlType.PressKey);
				CLING_TO_CLIFF_CONTROL = builder.defineEnum("cling-to-cliff_control", ClingToCliff.ControlType.PressKey);
				ENABLE_DOUBLE_TAPPING_FOR_DODGE = builder.define("enable_double_tap_dodge", false);
				ENABLE_CRAWL_IN_AIR = builder.define("enable_crawl_in_air", true);
				ENABLE_VAULT_IN_AIR = builder.define("enable_vault_in_air", true);
				CAN_GET_OFF_STEPS_WHILE_DODGE = builder.define("enable_getting_off_steps_while_dodge", false);
				VAULT_NEED_KEY_PRESSED = builder.define("vault_needs_key_pressed", false);
				HIDE_IN_BLOCK_NEED_SNEAK = builder.define("hide_in_block_needs_sneak", false);
				SUBSTITUTE_SPRINT_FOR_FAST_RUN = builder.comment("players can do actions needing FastRun with normal sprint").define("substitute_sprint", false);
				ACCEPTABLE_ANGLE_OF_WALL_JUMP = builder.comment("How much angle to wall is acceptable for triggering wall jump").defineInRange("wall_jump_acceptable_angle", 110, 0, 180);
			}
			builder.pop();
			builder.push("Stamina");
			{
				STAMINA_TYPE = builder.defineEnum("used_stamina", IStamina.Type.Default);
				builder.comment("Caution : Max stamina and stamina recovery config is removed because they became attributes.");
				builder.push("Consumption");
				{
					for (int i = 0; i < ActionGroup.ACTIONS.size(); i++) {
						staminaConsumptions[i]
								= builder.defineInRange(
								"stamina_consumption_of_" + ActionGroup.ACTIONS.get(i).getSimpleName(),
								ActionGroup.ACTION_REGISTRIES.get(i).getDefaultStaminaConsumption(),
								0, 10000
						);
					}
				}
				builder.pop();
			}
			builder.pop();
			builder.push("Other");
			{
				VAULT_ANIMATION_MODE = builder.comment("Vault Animation(Dynamic is to select animation dynamically)").defineEnum("vault_animation_mode", Vault.TypeSelectionMode.Dynamic);
				GUI_COLOR_THEME = builder.comment("Color theme of Setting GUI").defineEnum("gui_color_theme", ColorTheme.Blue);
				ENABLE_ACTION_SOUNDS = builder.define("enable_sounds", true);
				ENABLE_3D_RENDERING_FOR_ZIPLINE = builder.define("enable_zipline_3d_rendering", true);
				SHOW_AUTO_RESYNCHRONIZATION_NOTIFICATION = builder.define("show_auto_resync_notification", false);
				PARCOOL_IS_ACTIVE = builder.define("parcool_is_active", true);
			}
			builder.pop();
			BUILT_CONFIG = builder.build();
		}
	}

	public static final ConfigLimitation CLIENT_CONFIG_LIMITATION = new ConfigLimitation(true, new ForgeConfigSpec.Builder());
	public static final ConfigLimitation SERVER_CONFIG_LIMITATION = new ConfigLimitation(false, new ForgeConfigSpec.Builder());

	public static class ConfigLimitation {
		public ForgeConfigSpec getBuiltConfig() {
			return builtConfig;
		}

		private final ForgeConfigSpec builtConfig;
		private final List<ForgeConfigSpec.BooleanValue> booleans;
		private final List<ForgeConfigSpec.IntValue> integers;
		private final List<ForgeConfigSpec.DoubleValue> reals;
		@Nullable
		private final ForgeConfigSpec.BooleanValue enabled;

		public ForgeConfigSpec.BooleanValue get(ILimitationEntry.Bool entry) {
			return booleans.get(entry.index());
		}

		public ForgeConfigSpec.IntValue get(ILimitationEntry.Int entry) {
			return integers.get(entry.index());
		}

		public ForgeConfigSpec.DoubleValue get(ILimitationEntry.Real entry) {
			return reals.get(entry.index());
		}

		public void setEnabled(boolean enabled) {
			if (this.enabled != null) this.enabled.set(enabled);
		}

		public boolean isEnabled() {
			if (enabled != null) return enabled.get();
			else return true;
		}

		private ConfigLimitation(boolean alwaysEnabled, ForgeConfigSpec.Builder builder) {
			if (!alwaysEnabled) {
				enabled = builder.define("enabled", true);
			} else {
				enabled = null;
			}
			builder.push("bool");
			{
				booleans = LimitationEntries.Bool.ENTRIES.stream().map(e -> builder.comment(e.description()).define(
						e.name(), (boolean) e.getLowestPriorityValue()
				)).toList();
			}
			builder.pop();
			builder.push("int");
			{
				integers = LimitationEntries.Int.ENTRIES.stream().map(e -> builder.comment(e.description()).defineInRange(
						e.name(), e.getLowestPriorityValue(), e.min(), e.max()
				)).toList();
			}
			builder.pop();
			builder.push("real");
			{
				reals = LimitationEntries.Real.ENTRIES.stream().map(e -> builder.comment(e.description()).defineInRange(
						e.name(), e.getLowestPriorityValue(), e.min(), e.max()
				)).toList();
			}
			builder.pop();
			builtConfig = builder.build();
		}
	}
}
