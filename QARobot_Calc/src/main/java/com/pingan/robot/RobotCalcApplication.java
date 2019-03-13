package com.pingan.robot;

import com.pingan.robot.calc.service.InitVecModelService;
import com.pingan.robot.common.utils.SpringBootUtil;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ApplicationContext;

import java.io.File;

@SpringBootApplication
@EnableEurekaClient
public class RobotCalcApplication {

    public static void main(String[] args) {
        ApplicationContext app = SpringApplication.run(RobotCalcApplication.class, args);
        SpringBootUtil.setApplicationContext(app);
        InitVecModelService initVecModelService = SpringBootUtil.getBean(InitVecModelService.class);
        initVecModelService.initDocVecFromDB();
    }

}

