package com.zenith.via.handler;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.exception.CancelCodecException;
import com.viaversion.viaversion.exception.CancelEncoderException;
import com.zenith.network.client.ClientSession;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class ZViaEncodeHandler extends MessageToMessageEncoder<ByteBuf> {
    private final UserConnection info;
    private final ClientSession client;

    public ZViaEncodeHandler(UserConnection info, final ClientSession client) {
        this.info = info;
        this.client = client;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf bytebuf, List<Object> out) throws Exception {
        try {
            if (!info.checkOutgoingPacket()) throw CancelEncoderException.generate(null);
            if (!info.shouldTransformPacket()) {
                out.add(bytebuf.retain());
                return;
            }

            ByteBuf transformedBuf = ctx.alloc().buffer().writeBytes(bytebuf);
            try {
                info.transformOutgoing(transformedBuf, CancelEncoderException::generate);
                out.add(transformedBuf.retain());
            } finally {
                transformedBuf.release();
            }
        } catch (final Exception e) {
            if (e instanceof CancelCodecException || this.client.callPacketError(e)) return;
            throw e;
        }
    }
}
