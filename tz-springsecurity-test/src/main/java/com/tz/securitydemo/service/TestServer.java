package com.tz.securitydemo.service;

import com.tz.securitydemo.bean.RespBean;
import com.tz.securitydemo.entity.Users;
import com.tz.securitydemo.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 类功能简述：
 * 类功能详述：
 *
 * @author tianzheng
 * @date 2020/2/20 17:36
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class TestServer implements LogoutHandler {
    @Autowired
    private UserMapper userMapper;

    public RespBean getUserByName(String name) {
        return RespBean.ok("查询成功", userMapper.getUser(name));
    }

    public RespBean createUser(Users user) throws Exception {
        BCryptPasswordEncoder b = new BCryptPasswordEncoder();
        user.setUserPWD(b.encode(user.getPassword()));
        user.setUserRole("ROLE_" + user.getUserRole());
        userMapper.createUser(user);

        //throw new Exception();

        return RespBean.ok("用户创建成功",user);
    }

    @Override
    public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) {

    }

    public RespBean getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        RespBean resp = null;
        //有登陆用户就返回登录用户，没有就返回null
        if (authentication != null) {
            if (authentication instanceof AnonymousAuthenticationToken) {
                resp = RespBean.error("用户未登录");
                return resp;
            }

            if (authentication instanceof UsernamePasswordAuthenticationToken) {
                resp = RespBean.ok("查询成功", authentication.getPrincipal());
                return resp;
            }
        }

        return resp;
    }

    public void logout() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }
}
