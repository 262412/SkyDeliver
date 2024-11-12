package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface UserMapper {
    User getByOpenid(String openid);

    void insert(User user);

    User getById(Long userId);

    Integer countUserByMap(Map map);

    Integer countByMap(Map map);
}
