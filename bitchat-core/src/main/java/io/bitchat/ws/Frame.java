package io.bitchat.ws;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author houyi
 *
 * <p>
 * The structure of a Packet is like blow:
 * +----------+----------+----------------------------+
 * |  size    |  value   |  intro                     |
 * +----------+----------+----------------------------+
 * | 1 bytes  | 0xBF     |  magic number              |
 * | 1 bytes  |          |  the type 1:req 2:res 3:cmd|
 * | 4 bytes  |          |  content length            |
 * | ? bytes  |          |  the content               |
 * +----------+----------+----------------------------+
 * </p>
 *
 */
@Data
public class Frame implements Serializable {

    /**
     * the magic number of frame
     * 0xBF means BitChat Frame
     */
    public static byte FRAME_MAGIC = (byte) 0xBF;

    /**
     * magic number
     */
    private byte magic = Frame.FRAME_MAGIC;

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

    // Request
    /**
     * the name of the service
     */
    private String serviceName;

    /**
     * the name of the method
     */
    private String methodName;

    /**
     * the request params
     */
    private Map<String, Object> params;


    // Payload
    /**
     * the status of request
     */
    private boolean success;

    /**
     * the code of request
     */
    private int code;

    /**
     * the errorMsg of request
     */
    private String msg;

    /**
     * the result of request
     */
    private Object result;


    // Command
    /**
     * the name of the command
     */
    private String commandName;

    /**
     * the command content
     */
    private Object content;


}
