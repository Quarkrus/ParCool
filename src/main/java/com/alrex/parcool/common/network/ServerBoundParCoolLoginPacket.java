package com.alrex.parcool.common.network;

import com.alrex.parcool.common.info.CompiledLimitation;

public record ServerBoundParCoolLoginPacket(CompiledLimitation clientLimitation) {
}
