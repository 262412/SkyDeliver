package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";
    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private UserMapper userMapper;

    /**
     * 微信登录方法
     * 通过用户提供的登录码获取微信openid，并根据openid处理用户登录或注册
     *
     * @param userLoginDTO 用户登录数据传输对象，包含用户提供的登录码
     * @return 返回登录或新注册的用户对象
     * @throws LoginFailedException 如果获取openid失败，抛出登录失败异常
     */
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        // 获取微信openid
        String openid = getOpenid(userLoginDTO.getCode());
        // 如果openid为空，抛出登录失败异常
        if(openid == null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        // 根据openid查询用户数据库
        User user = userMapper.getByOpenid(openid);
        // 如果用户不存在，创建并插入新用户
        if(user == null){
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }
        // 返回用户对象
        return user;
    }

    /**
     * 获取微信openid
     * 通过微信登录接口，使用小程序提供的临时登录码code获取用户的唯一标识openid
     *
     * @param code 小程序前端提供的临时登录码
     * @return 返回用户的openid，如果获取失败返回null
     */
    private String getOpenid(String code) {
        // 创建参数映射，包含微信登录所需的信息
        Map<String, String> map = new HashMap<>();
        map.put("appid", weChatProperties.getAppid());
        map.put("secret", weChatProperties.getSecret());
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");
        // 发起HTTP GET请求到微信服务器
        String json = HttpClientUtil.doGet(WX_LOGIN, map);
        // 解析JSON响应，提取openid
        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");
        String errcode = jsonObject.getString("errcode");
        String errmsg = jsonObject.getString("errmsg");

        if (openid == null) {
            // 处理微信服务器返回的错误信息
            if (errcode != null && errmsg != null) {
                log.error("微信登录失败，错误码: {}, 错误信息: {}", errcode, errmsg);
                throw new LoginFailedException("微信登录失败，错误码: " + errcode + ", 错误信息: " + errmsg);
            } else {
                log.error("微信登录失败，未获取到openid");
                throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
            }
        }
        // 返回openid
        return openid;
    }
}
