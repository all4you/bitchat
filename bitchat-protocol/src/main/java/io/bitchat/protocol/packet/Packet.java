package io.bitchat.protocol.packet;

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
 * | 4 bytes  |          |  packet symbol             |
 * | 4 bytes  |          |  content length            |
 * | ? bytes  |          |  the content               |
 * +----------+----------+----------------------------+
 * </p>
 *
 * <p>
 * The usage of packet symbol:
 * +----------+----------+----------------------------+
 * |  symbol  |  into                                 |
 * +----------+----------+----------------------------+
 * | 0xxx     |  a system defined packet              |
 * | 1xxx     |  a request packet                     |
 * | 2xxx     |  a response packet                    |
 * | 3xxx     |  a one way packet                     |
 * | 4xxx     |  a reserved packet                    |
 * | 5xxx     |  an error packet                      |
 * +----------+----------+----------------------------+
 * </p>
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
     * the unique id
     */
    private long id;

    /**
     * magic number
     */
    private byte magic = Packet.PACKET_MAGIC;

    /**
     * whether handler the packet
     * in async way
     */
    private byte async = async();

    /**
     * the packet symbol
     * this field is used to
     * determinate which PacketHandler
     * should be chosen to handle
     * the packet
     */
    private int symbol = symbol();

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
    public abstract byte async();

    /**
     * <p>
     * specify the packet symbol
     * use packet symbol to assign
     * the detailed Packet
     * </p>
     *
     * <p>
     * when we decode the ByteBuf
     * we will find the actual Packet
     * the ByteBuf will be deserialize to
     * according to the packet symbol
     * </p>
     *
     * @return the packet symbol
     */
    public abstract int symbol();

}
