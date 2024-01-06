package com.zenith.network.client;

import com.github.steveice10.packetlib.packet.PacketProtocol;
import com.github.steveice10.packetlib.tcp.TcpClientSession;
import com.zenith.Proxy;
import com.zenith.network.netty.ZenithClientInboundChannelHandler;
import com.zenith.network.netty.ZenithClientOutboundChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.kyori.adventure.text.Component;

import java.io.IOException;

import static com.zenith.Shared.CLIENT_LOG;


@Getter
@Setter
public class ClientSession extends TcpClientSession {
    protected final Proxy proxy;
    protected boolean serverProbablyOff;
    protected long ping = 0L;

    private boolean inQueue = false;
    private int lastQueuePosition = Integer.MAX_VALUE;
    // in game
    private boolean online = false;
    private boolean disconnected = true;

    public ClientSession(String host, int port, String bindAddress, PacketProtocol protocol, @NonNull Proxy proxy) {
        super(host, port, bindAddress, 0, protocol);
        this.proxy = proxy;
        this.addListener(new ClientListener(this));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.pipeline().addAfter("manager", "zenith-client-in", new ZenithClientInboundChannelHandler(this));
        ctx.pipeline().addBefore("manager", "zenith-client-out", new ZenithClientOutboundChannelHandler(this));
        super.channelActive(ctx);
    }

    @Override
    public void disconnect(Component reason, Throwable cause) {
        super.disconnect(reason, cause);
        serverProbablyOff = false;
        if (cause == null) {
            serverProbablyOff = true;
        } else if (cause instanceof IOException)    {
            CLIENT_LOG.error("Error during client disconnect", cause);
        } else {
            CLIENT_LOG.error("", cause);
        }
        this.online = false;
    }

    @Override
    public void connect(boolean wait) {
        super.connect(wait);
    }
}
