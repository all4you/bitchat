package io.bitchat.im.server.service.user;

import cn.hutool.core.util.StrUtil;
import io.bitchat.im.BaseResult;
import io.bitchat.im.PojoResult;
import io.bitchat.im.lang.CommonConstants;
import io.bitchat.im.server.dao.user.BcUserMapper;
import io.bitchat.im.server.entity.user.BcUserDO;
import io.bitchat.im.server.processor.login.LoginRequest;
import io.bitchat.im.server.processor.register.RegisterRequest;
import io.bitchat.im.user.User;
import io.bitchat.lang.constants.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

/**
 * @author houyi
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DefaultUserService implements UserService {

    private final BcUserMapper userMapper;

    @Override
    public BaseResult register(RegisterRequest registerRequest) {
        BaseResult baseResult = new BaseResult();
        String userName = registerRequest.getUserName();
        String password = registerRequest.getPassword();
        if (StrUtil.isBlank(userName) || StrUtil.isBlank(password)) {
            baseResult.setErrorMessage(ResultCode.PARAM_INVALID.getCode(), "用户名密码不能为空");
            return baseResult;
        }
        try {
            int cnt = getBcUserCnt(userName);
            if (cnt > 0) {
                baseResult.setErrorMessage(ResultCode.RECORD_ALREADY_EXISTS.getCode(), "用户已存在");
                return baseResult;
            }
            saveBcUser(userName, password);
        } catch (Exception e) {
            baseResult.setErrorMessage(ResultCode.INTERNAL_ERROR.getCode(), "注册出错");
            return baseResult;
        }
        return baseResult;
    }

    @Override
    public PojoResult<User> login(LoginRequest loginRequest) {
        PojoResult<User> pojoResult = new PojoResult<>();
        String userName = loginRequest.getUserName();
        String password = loginRequest.getPassword();
        if (StrUtil.isBlank(userName) || StrUtil.isBlank(password)) {
            pojoResult.setErrorMessage(ResultCode.PARAM_INVALID.getCode(), "用户名密码不能为空");
            return pojoResult;
        }
        try {
            BcUserDO userDO = getBcUser(userName, password);
            boolean success = userDO != null;
            if (!success) {
                pojoResult.setErrorMessage(ResultCode.PARAM_INVALID.getCode(), "用户名密码错误");
                return pojoResult;
            }
            pojoResult.setContent(User.builder()
                    .userId(userDO.getId())
                    .userName(userName)
                    .password(password)
                    .build());
        } catch (Exception e) {
            pojoResult.setErrorMessage(ResultCode.INTERNAL_ERROR.getCode(), "登录出错");
            return pojoResult;
        }
        return pojoResult;
    }

    private int getBcUserCnt(String userName) {
        Example example = new Example(BcUserDO.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isDeleted", CommonConstants.NOT_DELETED);
        criteria.andEqualTo("userName", userName);
        return userMapper.selectCountByExample(example);
    }

    private BcUserDO getBcUser(String userName, String password) {
        Example example = new Example(BcUserDO.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isDeleted", CommonConstants.NOT_DELETED);
        criteria.andEqualTo("userName", userName);
        criteria.andEqualTo("password", password);
        return userMapper.selectOneByExample(example);
    }

    private void saveBcUser(String userName, String password) {
        BcUserDO record = BcUserDO.builder()
                .userName(userName)
                .password(password)
                .build();
        userMapper.insertSelective(record);
    }

}
