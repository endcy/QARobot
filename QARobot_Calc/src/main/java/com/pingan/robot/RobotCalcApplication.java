package com.pingan.robot;

import com.pingan.robot.calc.service.InitModel;
import com.pingan.robot.common.utils.SpringBootUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@EnableEurekaClient
public class RobotCalcApplication {

    public static void main(String[] args) {
        ApplicationContext app = SpringApplication.run(RobotCalcApplication.class, args);
        SpringBootUtil.setApplicationContext(app);
        InitModel.getDocModel();//初始化模型
    }

}

