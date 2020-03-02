package com.tz.securitydemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
//@EnableSwagger2
@MapperScan("com.tz.securitydemo.mapper")
public class TZSecurityDemo {

    public static void main(String[] args) {
        SpringApplication.run(TZSecurityDemo.class, args);
    }

}
