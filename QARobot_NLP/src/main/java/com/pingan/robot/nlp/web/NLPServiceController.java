package com.pingan.robot.nlp.web;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/nlp")
public class NLPServiceController {
    private final Logger logger = Logger.getLogger(getClass());

    @CrossOrigin
    @RequestMapping(value = "/segment/get")
    //restful形式的url样式只支持get可用约束注解GetMapping，类似post为PostMapping
    //@GetMapping("/keyword/get")
    public List<String> segment(@RequestParam("content") String str) {
        List<String> productList = new ArrayList<>();
        productList.add("肥皂");
        productList.add("可乐");
        return productList;
    }



}
