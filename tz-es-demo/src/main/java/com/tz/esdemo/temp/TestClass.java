package com.tz.esdemo.temp;

import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;

/**
 * 类功能简述：
 * 类功能详述：
 *
 * @author tianzheng
 * @date 2019/12/31 17:44
 */
public class TestClass {
    private final Logger logger = Logger.getLogger(getClass());

    @PostConstruct
    private void init(){
        logger.info("===========3333333333=============");
    }
}
