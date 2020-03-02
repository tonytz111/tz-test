package com.tz.securitydemo.service;

import com.tz.securitydemo.entity.Users;
import com.tz.securitydemo.jwtsecurity.JwtTokenUtil;
import com.tz.securitydemo.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 类功能简述：
 * 类功能详述：
 *
 * @author tianzheng
 * @date 2020/2/20 11:55
 */
@Service
@Slf4j
public class UserService implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtTokenUtil tokenUtil;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Users user = userMapper.getUser(userName);
        if (user == null) {
            throw new UsernameNotFoundException("用户名不正确");
        }
        String token = tokenUtil.generateToken(user);
        log.debug("=======Token is:" + token);
        user.setToken(token);
        return user;
    }
}
