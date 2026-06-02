package com.alrex.parcool.common.handlers;

import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ActionExtension;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class InputHandler {
    @SubscribeEvent
    public static void onInput(InputEvent.InteractionKeyMappingTriggered event) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;
        var parkourability = Parkourability.get(player);
        parkourability.getAdditionalProperties().onJump();
        for (var listener : parkourability.getActions().getExtensionListeners(ActionExtension.KeyMapTriggeredListener.class)) {
            listener.onInput(event);
        }
    }
}
