package io.bitchat.protocol;

import io.bitchat.protocol.serialize.Serializer;

/**
 * <p>
 * Choose a Serializer according to the serialize algorithm
 * </p>
 *
 * @author houyi
 */
public interface SerializerChooser {

    /**
     * choose a Serializer
     *
     * @param serializeAlgorithm the serialize algorithm
     * @return the Serializer
     */
    Serializer choose(byte serializeAlgorithm);

}
