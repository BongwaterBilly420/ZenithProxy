package com.zenith.network.client.handler.incoming.level;

import com.github.steveice10.mc.protocol.packet.ingame.clientbound.level.ClientboundSetChunkCacheRadiusPacket;
import com.zenith.network.client.ClientSession;
import com.zenith.network.registry.AsyncPacketHandler;

import static com.zenith.Shared.CACHE;

public class SetChunkCacheRadiusHandler implements AsyncPacketHandler<ClientboundSetChunkCacheRadiusPacket, ClientSession> {
    @Override
    public boolean applyAsync(final ClientboundSetChunkCacheRadiusPacket packet, final ClientSession session) {
        CACHE.getChunkCache().setServerViewDistance(packet.getViewDistance());
        return true;
    }
}
