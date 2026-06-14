package com.alrex.parcool.proxy;

import com.alrex.parcool.common.handlers.LoginLogoutHandler;
import com.alrex.parcool.common.handlers.PlayerCloneHandler;
import com.alrex.parcool.common.handlers.PlayerEventHandler;
import com.alrex.parcool.server.command.CommandRegistry;
import com.alrex.parcool.server.limitation.LimitationRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.simple.SimpleChannel;

public abstract class CommonProxy {
    public boolean ParCoolIsActive() {
        return true;
    }
	public abstract void registerMessages(SimpleChannel instance);

	public void init() {
        MinecraftForge.EVENT_BUS.register(LoginLogoutHandler.class);
        MinecraftForge.EVENT_BUS.register(PlayerCloneHandler.class);
        MinecraftForge.EVENT_BUS.register(CommandRegistry.class);
        MinecraftForge.EVENT_BUS.register(LimitationRegistry.class);
        MinecraftForge.EVENT_BUS.register(PlayerEventHandler.class);
	}
}
