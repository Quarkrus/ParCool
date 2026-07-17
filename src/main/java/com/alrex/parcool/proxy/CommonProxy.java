package com.alrex.parcool.proxy;

import com.alrex.parcool.common.handlers.PlayerEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.simple.SimpleChannel;

public abstract class CommonProxy {
	public abstract void registerMessages(SimpleChannel instance);

	public void init() {
        MinecraftForge.EVENT_BUS.register(PlayerEventHandler.class);
	}
}
