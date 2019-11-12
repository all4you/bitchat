package io.bitchat.im.server.processor.register;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author houyi
 */
@Data
@NoArgsConstructor
public class RegisterRequest {

    private String userName;

    private String password;

}
