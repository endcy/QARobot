package com.pingan.robot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class RobotEurekaApplication {

    public static void main(String[] args) {
        SpringApplication.run(RobotEurekaApplication.class, args);
    }

}
