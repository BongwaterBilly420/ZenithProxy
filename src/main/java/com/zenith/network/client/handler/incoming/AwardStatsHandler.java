package com.zenith.network.client.handler.incoming;

import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundAwardStatsPacket;
import com.zenith.network.client.ClientSession;
import com.zenith.network.registry.AsyncPacketHandler;
import lombok.NonNull;

import static com.zenith.Shared.CACHE;

public class AwardStatsHandler implements AsyncPacketHandler<ClientboundAwardStatsPacket, ClientSession> {
    @Override
    public boolean applyAsync(@NonNull ClientboundAwardStatsPacket packet, @NonNull ClientSession session) {
        CACHE.getStatsCache().getStatistics().putAll(packet.getStatistics()); //cache all locally
        return true;
    }
}
