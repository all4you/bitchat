package io.bitchat.ws;

import io.bitchat.core.id.IdFactory;
import io.bitchat.core.id.SnowflakeIdFactory;
import io.bitchat.packet.PacketType;
import io.bitchat.packet.Payload;

/**
 * @author houyi
 */
public class FrameFactory {

    private static IdFactory idFactory = SnowflakeIdFactory.getInstance();

    public static Frame newResponseFrame(Payload payload, String id) {
        Frame frame = new Frame();
        frame.setId(id);
        frame.setType(PacketType.PACKET_TYPE_RESPONSE);
        frame.setSuccess(payload.isSuccess());
        frame.setCode(payload.getCode());
        frame.setMsg(payload.getMsg());
        frame.setResult(payload.getResult());
        return frame;
    }

    public static Frame newCmdFrame(String commandName, Object content) {
        // command frame
        Frame frame = new Frame();
        frame.setId(String.valueOf(idFactory.nextId()));
        frame.setType(PacketType.PACKET_TYPE_COMMAND);
        frame.setCommandName(commandName);
        frame.setContent(content);
        return frame;
    }


}
