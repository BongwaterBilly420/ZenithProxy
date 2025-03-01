package com.zenith.network.client.handler.incoming.entity;

import com.github.steveice10.mc.protocol.packet.ingame.clientbound.entity.ClientboundUpdateMobEffectPacket;
import com.zenith.cache.data.entity.Entity;
import com.zenith.cache.data.entity.EntityLiving;
import com.zenith.cache.data.entity.PotionEffect;
import com.zenith.network.client.ClientSession;
import com.zenith.network.registry.AsyncPacketHandler;
import lombok.NonNull;

import static com.zenith.Shared.CACHE;
import static com.zenith.Shared.CLIENT_LOG;

public class UpdateMobEffectHandler implements AsyncPacketHandler<ClientboundUpdateMobEffectPacket, ClientSession> {
    @Override
    public boolean applyAsync(@NonNull ClientboundUpdateMobEffectPacket packet, @NonNull ClientSession session) {
        try {
            Entity entity = CACHE.getEntityCache().get(packet.getEntityId());
            if (entity instanceof EntityLiving) {
                ((EntityLiving) entity).getPotionEffectMap().put(packet.getEffect(), new PotionEffect(
                    packet.getEffect(),
                    packet.getAmplifier(),
                    packet.getDuration(),
                    packet.isAmbient(),
                    packet.isShowParticles(),
                    packet.getFactorData()
                ));
            } else {
                CLIENT_LOG.debug("Received ServerEntityEffectPacket for invalid entity (id={})", packet.getEntityId());
                return false;
            }
        } catch (ClassCastException e)  {
            CLIENT_LOG.debug("Received ServerEntityEffectPacket for non-equipment entity (id={})", packet.getEntityId(), e);
            return false;
        }
        return true;
    }
}
