package com.pingan.robot.common.log;

import org.slf4j.Logger;
import org.slf4j.MDC;

import java.lang.management.ManagementFactory;

/**
 * @Description
 * @Author chenxx
 * @Date 2019/1/22 16:41
 **/
public class PALogUtil {

    public static void defaultErrorInfo(Logger logger, Exception e) {
        e.printStackTrace();
        String name = ManagementFactory.getRuntimeMXBean().getName();
        String pid = name.split("@")[0];
        String msg = (e == null || e.getMessage() == null) ? "Error" : e.getMessage();
        MDC.put("pid", pid);
        MDC.put("currentstate", "1");
        logger.error(msg);
    }
}
