package io.bitchat.protocol;

import io.bitchat.core.id.IdFactory;
import io.bitchat.core.id.SnowflakeIdFactory;
import io.bitchat.lang.constants.ServiceName;

/**
 * @author houyi
 */
public class PacketFactory {

    private static IdFactory idFactory = SnowflakeIdFactory.getInstance();

    public static Packet newRequestPacket(Request request, long id) {
        Packet packet = new DefaultPacket();
        packet.setId(id);
        packet.setType(PacketType.PACKET_TYPE_REQUEST);
        packet.setRequest(request);
        return packet;
    }

    public static Packet newResponsePacket(Payload payload, long id) {
        Packet packet = new DefaultPacket();
        packet.setId(id);
        packet.setType(PacketType.PACKET_TYPE_RESPONSE);
        packet.setPayload(payload);
        return packet;
    }

    public static Packet newCmdPacket(Request request) {
        // command packet can not provide id
        Packet packet = new DefaultPacket();
        packet.setType(PacketType.PACKET_TYPE_COMMAND);
        packet.setRequest(request);
        return packet;
    }

    public static Packet newPingPacket() {
        Request request = new Request();
        request.setServiceName(ServiceName.HEART_BEAT);
        Packet packet = PacketFactory.newRequestPacket(request, idFactory.nextId());
        packet.setHandleAsync(false);
        return packet;
    }




}
