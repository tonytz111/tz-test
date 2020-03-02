package com.tz.securitydemo.controller;

import com.tz.securitydemo.bean.RespBean;
import com.tz.securitydemo.entity.Users;
import com.tz.securitydemo.service.TestServer;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.bind.annotation.*;

/**
 * 类功能简述：
 * 类功能详述：
 *
 * @author tianzheng
 * @date 2020/2/20 17:39
 *
 */
@Slf4j
@RestController
public class TestController {
    @Autowired
    private TestServer server;

    @Autowired
    private LogoutHandler logout;

    @ApiOperation(value = "查询用户", notes = "拥有相应权限的用户允许查询用户")
    @RequestMapping(value = "query", method = RequestMethod.GET)
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public RespBean getUserByName(@RequestParam String name) {
        log.debug("=====getUserByName===");
        return server.getUserByName(name);
    }

    @ApiOperation(value = "创建用户", notes = "拥有admin权限的用户允许创建用户")
    @RequestMapping(value = "create", method = RequestMethod.POST)
    //@PreAuthorize("#user.userRole.equals('ADMIN')")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public RespBean createUser(Users user) throws Exception {
        return server.createUser(user);
    }

    @ApiOperation(value = "查询当前登录用户", notes = "查询当前登录的用户的详细信息")
    @RequestMapping(value = "queryLogin", method = RequestMethod.GET)
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public RespBean getLoginUser() {
        return server.getLoginUser();
    }

    @ApiOperation(value = "test", notes = "test")
    @RequestMapping(value = "test", method = RequestMethod.POST)
    @PreAuthorize("hasAnyRole('USER')")
    public RespBean test() {
        return RespBean.ok("请求成功");
    }

    @ApiOperation(value = "用户登出", notes = "用户登出")
    @RequestMapping(value = "userLogout", method = RequestMethod.GET)
    public void userLogout() {

    }


}
