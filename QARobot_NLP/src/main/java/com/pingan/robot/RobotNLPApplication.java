package com.pingan.robot;

import com.pingan.robot.common.utils.SpringBootUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@EnableEurekaClient
public class RobotNLPApplication {

    public static void main(String[] args) {
        ApplicationContext app = SpringApplication.run(RobotNLPApplication.class, args);
        SpringBootUtil.setApplicationContext(app);

    }

}

