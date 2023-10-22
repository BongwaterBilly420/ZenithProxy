package com.zenith.network.client.handler.outgoing;

import com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundChatCommandPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundChatPacket;
import com.zenith.event.module.OutboundChatEvent;
import com.zenith.network.client.ClientSession;
import com.zenith.network.registry.OutgoingHandler;

import static com.zenith.Shared.EVENT_BUS;

public class OutgoingChatHandler implements OutgoingHandler<ServerboundChatPacket, ClientSession> {
    @Override
    public ServerboundChatPacket apply(final ServerboundChatPacket packet, final ClientSession session) {
        // allow us to dispatch commands just with chat packets
        if (packet.getMessage().startsWith("/")) {
            String message = packet.getMessage();
            session.send(new ServerboundChatCommandPacket(message.substring(1, (Math.min(message.length(), 257)))));
            return null;
        }
        final OutboundChatEvent outboundChatEvent = new OutboundChatEvent(packet);
        EVENT_BUS.post(outboundChatEvent);
        if (outboundChatEvent.isCancelled()) return null;
        return packet;
    }
}
