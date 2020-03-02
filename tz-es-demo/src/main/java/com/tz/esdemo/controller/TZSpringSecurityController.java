package com.tz.esdemo.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 类功能简述：
 * 类功能详述：
 *
 * @author tianzheng
 * @date 2020/2/17 10:10
 */
@RestController
@RequestMapping("/securityTest")
public class TZSpringSecurityController {

    @GetMapping("/securicty")
    public String testSecuricty() {
        BCryptPasswordEncoder a = new BCryptPasswordEncoder();

        return a.encode("123");
    }

}
