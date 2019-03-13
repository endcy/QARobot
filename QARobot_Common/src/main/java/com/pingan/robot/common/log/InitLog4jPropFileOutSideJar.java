package com.pingan.robot.common.log;

import org.apache.log4j.PropertyConfigurator;

import java.io.File;

/**
 * @Description
 * @Author chenxx
 * @Date 2019/3/13 16:58
 **/
public class InitLog4jPropFileOutSideJar {
    static {
        //log4j.porperties放置到jar之外与jar同级的config目录中 让ApplicationMain类继承此类
        //PropertyConfigurator.configure(System.getProperty("user.dir") + File.separator + "config" + File.separator + "log4j.properties");
    }
}
