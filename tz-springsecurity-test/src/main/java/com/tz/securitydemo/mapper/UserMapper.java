package com.tz.securitydemo.mapper;

import com.tz.securitydemo.entity.Users;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    public Users getUser(String userName);

    public void createUser(Users user);
}
