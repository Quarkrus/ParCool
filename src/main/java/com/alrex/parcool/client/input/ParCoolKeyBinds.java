package com.alrex.parcool.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

@OnlyIn(Dist.CLIENT)
public class ParCoolKeyBinds {
	public record Input(KeyMapping key, InputState state) {
		private static Input from(KeyMapping key) {
			return new Input(key, new InputState());
		}

		private void update() {
			state.update(key);
		}
	}

	public static class InputState {
		private int pressedDurationTick = 0;
		private int notPressedDurationTick = 0;

		private void update(KeyMapping key) {
			if (key.isDown()) {
				pressedDurationTick++;
				notPressedDurationTick = -1;
			} else {
				pressedDurationTick = -1;
				notPressedDurationTick++;
			}
		}

		public int getPressedDuration() {
			return pressedDurationTick;
		}

		public int getNotPressedDuration() {
			return notPressedDurationTick;
		}

		public boolean isJustPressed() {
			return pressedDurationTick == 0;
		}

		public boolean isJustReleased() {
			return notPressedDurationTick == 0;
		}
	}

	private static final ArrayList<Input> REGISTERED_KEYS = new ArrayList<>();
	private static final ArrayList<Input> LISTENING_KEYS = new ArrayList<>();

	private static Input register(KeyMapping key) {
		var input = Input.from(key);
		REGISTERED_KEYS.add(input);
		return input;
	}

	private static Input listen(KeyMapping key) {
		var input = Input.from(key);
		LISTENING_KEYS.add(input);
		return input;
	}

	public static final String KEY_CATEGORY = "key.category.parcool";

	public static final Input CRAWL = register(new KeyMapping("key.parcool.crawl", GLFW.GLFW_KEY_C, KEY_CATEGORY));
	public static final Input HANG_ON = register(new KeyMapping("key.parcool.hang_on", InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT, KEY_CATEGORY));
	public static final Input SLIDE_DOWN = register(new KeyMapping("key.parcool.slide_down", InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT, KEY_CATEGORY));

	public static final Input JUMP = listen(Minecraft.getInstance().options.keyJump);
	public static final Input MOVEMENT_FORWARD = listen(Minecraft.getInstance().options.keyUp);
	public static final Input MOVEMENT_BACK = listen(Minecraft.getInstance().options.keyDown);
	public static final Input MOVEMENT_RIGHT = listen(Minecraft.getInstance().options.keyRight);
	public static final Input MOVEMENT_LEFT = listen(Minecraft.getInstance().options.keyLeft);

	public static void tick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.END) return;
		for (var input : REGISTERED_KEYS) {
			input.update();
		}
		for (var input : LISTENING_KEYS) {
			input.update();
		}
	}

	public static void registerAll(RegisterKeyMappingsEvent event) {
		for (var input : REGISTERED_KEYS) {
			event.register(input.key);
		}
		REGISTERED_KEYS.trimToSize();
		LISTENING_KEYS.trimToSize();
	}
}
