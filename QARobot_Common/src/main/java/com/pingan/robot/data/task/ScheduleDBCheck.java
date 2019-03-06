package com.pingan.robot.data.task;

import com.pingan.robot.common.utils.DateUtil;
import com.pingan.robot.data.dblink.MutilDBConPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;

@Configuration
//@Service
@EnableScheduling
public class ScheduleDBCheck {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${spring.datasource.isManualCheckDB}")
    private String isCheck;

    @Resource
    private MutilDBConPool dbConPool;

    @Scheduled(cron = "0 0/20 * * * ?") // 每20分钟执行一次
    public void activeDB() {
        if (!"1".equals(isCheck)) return;
        logger.info(DateUtil.getCurrentDateTimeStd() + " DB轮询心跳检测任务执行！");
        dbConPool.heat4Alive();
    }
}
