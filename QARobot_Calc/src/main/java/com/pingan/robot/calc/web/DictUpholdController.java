package com.pingan.robot.calc.web;

import com.pingan.robot.calc.service.InitVecModelService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Description
 * @Author chenxx
 * @Date 2019/3/29 15:25
 **/
@RestController
@RequestMapping("/dict")
public class DictUpholdController {
    @Resource
    private InitVecModelService vecModelService;


}
