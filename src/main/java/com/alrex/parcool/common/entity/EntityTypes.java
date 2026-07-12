package com.alrex.parcool.common.entity;

import com.alrex.parcool.ParCool;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityTypes {
    private static final DeferredRegister<EntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ParCool.MOD_ID);

    public static void register(IEventBus bus) {
        REGISTER.register(bus);
    }
}
