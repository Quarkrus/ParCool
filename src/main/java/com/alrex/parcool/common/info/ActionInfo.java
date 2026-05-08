package com.alrex.parcool.common.info;

public class ActionInfo {
    public ActionInfo() {
    }

    public CompiledLimitation getServerLimitation() {
        return serverLimitation;
	}

    public void setServerLimitation(CompiledLimitation serverLimitation) {
        this.serverLimitation = serverLimitation;
    }

    public CompiledLimitation getClientLimitation() {
        return clientLimitation;
    }

    public void setClientLimitation(CompiledLimitation clientLimitation) {
        this.clientLimitation = clientLimitation;
    }

    private CompiledLimitation serverLimitation = CompiledLimitation.UNSYNCED_INSTANCE;
    private CompiledLimitation clientLimitation = CompiledLimitation.UNSYNCED_INSTANCE;
}
