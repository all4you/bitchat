package io.bitchat.packet;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * A base abstract packet
 * Each Detailed Packet should extends the {@link Packet}
 * </p>
 *
 * <p>
 * The structure of a Packet is like blow:
 * +----------+----------+----------------------------+
 * |  size    |  value   |  intro                     |
 * +----------+----------+----------------------------+
 * | 1 bytes  | 0xBC     |  magic number              |
 * | 1 bytes  |          |  serialize algorithm       |
 * | 1 bytes  |          |  the type 1:req 2:res 3:cmd|
 * | 4 bytes  |          |  content length            |
 * | ? bytes  |          |  the content               |
 * +----------+----------+----------------------------+
 * </p>
 *
 *
 * @author houyi
 */
@Data
public abstract class Packet implements Serializable {

    /**
     * the magic number of packet
     * 0xBC means BitChat
     */
    public static byte PACKET_MAGIC = (byte) 0xBC;

    /**
     * magic number
     */
    private byte magic = Packet.PACKET_MAGIC;

    /**
     * the unique id
     */
    private long id;

    /**
     * <p>
     * specify the type of the packet
     * 1-request
     * 2-response
     * 3-command
     * </p>
     */
    private byte type;

    /**
     * the request model
     */
    private Request request;

    /**
     * the response
     */
    private Payload payload;

    /**
     * the command
     */
    private Command command;

    /**
     * <p>
     * the serialize algorithm
     * </p>
     */
    public byte algorithm = algorithm();

    /**
     * whether handler the packet
     * in async way
     */
    private boolean handleAsync = handleAsync();

    /**
     * <p>
     * specify the serialize algorithm
     * </p>
     *
     * <p>
     * the packet will be encode and decode
     * by the serialize algorithm
     * </p>
     *
     * @return the serialize algorithm
     */
    public abstract byte algorithm();

    /**
     * <p>
     * whether handle the packet
     * in async way
     * </p>
     *
     * @return the byte value of whether
     * handle packet in async way
     */
    public abstract boolean handleAsync();

}
