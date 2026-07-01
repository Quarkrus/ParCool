package com.alrex.parcool.client.hud.stamina;

import com.alrex.parcool.api.client.gui.StaminaDisplayContext;
import com.alrex.parcool.common.Parkourability;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;

public interface IStaminaHUD {
    void render(
            ForgeGui gui,
            PoseStack stack,
            Parkourability parkourability,
            StaminaDisplayContext currentContext, StaminaDisplayContext oldContext,
            float partialTick, int width, int height
    );

    default void tick(Player player, StaminaDisplayContext currentContext, StaminaDisplayContext oldContext) {
    }
}
