package io.bitchat.packet;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * <p>
 * A request model
 * </p>
 *
 * @author houyi
 */
@Data
@NoArgsConstructor
public class Request implements Serializable {

    /**
     * the name of the service
     */
    private String serviceName;

    /**
     * the name of the method
     * if null will use <b>doProcess()</b>
     * as default
     */
    private String methodName;

    /**
     * the request params
     */
    private Map<String, Object> params;

}
