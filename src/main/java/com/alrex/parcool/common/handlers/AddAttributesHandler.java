package com.alrex.parcool.common.handlers;


import com.alrex.parcool.api.ParCoolAttributes;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class AddAttributesHandler {
    @SubscribeEvent
    public static void onAddAttributes(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, ParCoolAttributes.MAX_STAMINA.get());
        event.add(EntityType.PLAYER, ParCoolAttributes.STAMINA_RECOVERY.get());
    }
}
