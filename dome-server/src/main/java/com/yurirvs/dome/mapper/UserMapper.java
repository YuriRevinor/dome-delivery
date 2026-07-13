package com.yurirvs.dome.mapper;

import com.yurirvs.dome.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE openid=#{openid}")
    User getByOpenid(String openid);

    void addUser(User user);

    @Select("SELECT * FROM user WHERE id=#{userId}")
    User getById(Long userId);
}
