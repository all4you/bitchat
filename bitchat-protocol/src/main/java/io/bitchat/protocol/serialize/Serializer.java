package io.bitchat.protocol.serialize;

/**
 * <p>
 * A Serializer which can serialize or deserialize object
 * </p>
 *
 * @author houyi
 */
public interface Serializer {

    /**
     * serialize the object
     *
     * @param object the object need to serialize
     * @return byte array
     */
    byte[] serialize(Object object);

    /**
     * deserialize the byte array
     *
     * @param bytes the byte array need to deserialize
     * @param clazz the class type byte array will deserialize to
     * @return object
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);

}
