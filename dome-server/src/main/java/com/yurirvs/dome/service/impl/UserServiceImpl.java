package com.yurirvs.dome.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yurirvs.dome.constant.MessageConstant;
import com.yurirvs.dome.dto.UserLoginDTO;
import com.yurirvs.dome.entity.User;
import com.yurirvs.dome.exception.LoginFailedException;
import com.yurirvs.dome.mapper.UserMapper;
import com.yurirvs.dome.properties.WeChatProperties;
import com.yurirvs.dome.service.UserService;
import com.yurirvs.dome.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    public static final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;


    @Override
    public User login(UserLoginDTO userLoginDTO) {

        String openid = getOpenid(userLoginDTO.getCode());

        if (openid == null) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        User user = userMapper.getByOpenid(openid);

        if (user == null) {
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();

            userMapper.addUser(user);
        }

        return user;
    }


    private String getOpenid(String code) {
        Map<String, String> params = new HashMap<>();
        params.put("appid", weChatProperties.getAppid());
        params.put("secret", weChatProperties.getSecret());
        params.put("js_code", code);
        params.put("grant_type", "authorization_code");

        String result = HttpClientUtil.doGet(WX_LOGIN_URL, params);
        JSONObject jsonObject = JSON.parseObject(result);
        return jsonObject.getString("openid");
    }
}
