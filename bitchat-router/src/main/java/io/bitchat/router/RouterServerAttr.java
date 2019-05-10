package io.bitchat.router;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * A Config which holds the address and port of a router server
 * </p>
 *
 * @author houyi
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouterServerAttr {
    private String address;
    private int port;
}
