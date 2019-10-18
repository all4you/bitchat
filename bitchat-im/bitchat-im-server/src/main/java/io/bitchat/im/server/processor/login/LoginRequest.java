package io.bitchat.im.server.processor.login;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author houyi
 */
@Data
@NoArgsConstructor
public class LoginRequest {

    private String userName;

    private String password;

}
