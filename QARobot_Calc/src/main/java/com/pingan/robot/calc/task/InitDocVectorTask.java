package com.pingan.robot.calc.task;

import com.pingan.robot.calc.service.InitVecModelService;
import com.pingan.robot.common.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;

/**
 * @Description 定时根据数据库问题库的更新向量数据模型
 * @Author chenxx
 * @Date 2019/3/7 10:05
 **/
public class InitDocVectorTask {
    @Configuration
//@Service
    @EnableScheduling
    public class ScheduleDBCheck {
        private final Logger logger = LoggerFactory.getLogger(getClass());

        @Value("${nlp.initDocVec.auto}")
        private String isFlesh;

        @Resource
        private InitVecModelService vecModelService;

        @Scheduled(cron = "0 0/30 * * * ?") // 每30分钟执行一次
        public void activeDB() {
            if (!"1".equals(isFlesh)) return;
            logger.info(DateUtil.getCurrentDateTimeStd() + " 定时刷新模型文本向量任务执行！……TODO");
//            vecModelService.;
        }
    }
}
