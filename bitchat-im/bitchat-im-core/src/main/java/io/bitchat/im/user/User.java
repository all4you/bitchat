package io.bitchat.im.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author houyi
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private int channelType;
    private Long userId;
    private String userName;
    private String password;
    private String nickName;
    private Byte gender;
    private String avatar;
    private String mobile;
}
