package com.pingan.robot.nlp.web;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/nlp")
public class NLPServiceController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @CrossOrigin
    @RequestMapping(value = "/segment/get")
    //restful形式的url样式只支持get可用约束注解GetMapping，类似post为PostMapping
    //@GetMapping("/segment/get")
    public List<String> segment(@RequestParam("content") String str) {
        logger.info("NLP segment/get receive content:{}", str);
        List<Term> segList = HanLP.segment(str);
        List<String> list = new ArrayList<>();
        for (Term term : segList)
            list.add(term.word);
        logger.info("NLP segment/get operation end!");
        return list;
    }

    @CrossOrigin
    @RequestMapping(value = "/keyword/get")
    //restful形式的url样式只支持get可用约束注解GetMapping，类似post为PostMapping
    //@GetMapping("/keyword/get")
    public List<String> keyword(@RequestParam("content") String str) {
        logger.info("NLP keyword/get receive content:{}", str);
        List<String> keywordList = HanLP.extractKeyword(str, 1);
        logger.info("NLP keyword/get operation end!");
        return keywordList;
    }


}
