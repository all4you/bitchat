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
     * JS中要创建一个long的数值比较麻烦
     * 所以改成String类型
     */
    private String id;

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
     * fix me
     * 如果通过protobuf js的库无法直接解析成Map
     * 所以通过在js中将参数转成JSON字符串
     * 然后再通过FastJson将字符串转成Map
     */
    private String paramJson;


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
    private String resultJson;


    // Command
    /**
     * the name of the command
     */
    private String commandName;

    /**
     * the command content
     */
    private String contentJson;


}
