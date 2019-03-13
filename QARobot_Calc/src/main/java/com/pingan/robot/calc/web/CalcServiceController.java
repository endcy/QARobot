package com.pingan.robot.calc.web;

import com.pingan.robot.calc.bean.DocVectorType;
import com.pingan.robot.calc.nlp.FixDocVectorModel;
import com.pingan.robot.calc.service.InitBaseVecModel;
import com.pingan.robot.common.log.PALogUtil;
import com.pingan.robot.common.utils.StringUtil;
import com.pingan.robot.common.vo.QAVO;
import com.pingan.robot.data.service.IContentService;
import com.pingan.robot.data.utils.ConstansDefination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/calc")
public class CalcServiceController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IContentService contentService;

    /**
     * 获取答案或相似问题
     */
    @CrossOrigin
    @RequestMapping(value = "/answer/get")
    public List<QAVO> getAnswer(@RequestParam("content") String content, @RequestParam("sysId") Integer sysId) {
        logger.info("CALC get receive question:{} sysId:{}", content, sysId);
        List<QAVO> qaListSim = new ArrayList<>();
        if (StringUtil.isEmpty(content)) {
            return qaListSim;
        }
        HashMap map = new HashMap();
        map.put("sysId", sysId);
        Map<Integer, QAVO> qaList = contentService.findAllMap(map);
        FixDocVectorModel docVectorModel = InitBaseVecModel.getDocVecModel(sysId, DocVectorType.Document);
        double maxRate = 0;
        QAVO target = null;
        int count = 3;  //只需要三个最相似问题
        try {
            List<Map.Entry<Integer, Float>> analyList = docVectorModel.nearest10Doc(content);
            for (Map.Entry<Integer, Float> entry : analyList) {   //todo 可覆盖方法 默认是相近的10个文本
                //qaList序号entry.getKey()和相似度entry.getValue()
                float rate = entry.getValue();
                if (count > 0 && rate >= 0.6) { //todo  set rate x
                    QAVO vo = qaList.get(entry.getKey());
                    vo.setSimRate(rate);
                    qaListSim.add(vo);
                    if (rate > maxRate) {
                        maxRate = rate;
                        target = vo;
                    }
                    count--;
                } else
                    break;
            }
            logger.info("question:{} 对比分析完成!", content);
        } catch (Exception e) {
            PALogUtil.defaultErrorInfo(logger, e);
            e.printStackTrace();
            return qaListSim;
        }
        if (maxRate >= 0.82) {       //todo set rate y
            logger.info("找到唯一答案，对应问题id：{} 相似度：{}", target.getId(), maxRate);
            QAVO targetHasAns = contentService.find(target); //设置DB IO，修改后取出的list不含有答案，所以需要再次取出标准答案
            targetHasAns.setRemark(ConstansDefination.STANDARD_ANSWER);
            targetHasAns.setSimRate((float) maxRate);
            qaListSim.clear();
            qaListSim.add(targetHasAns);
        }
        logger.info("CALC get operation end!");
        return qaListSim;
    }


    @CrossOrigin
    @RequestMapping(value = "/recommend/get")
    public List<QAVO> getRecommend(@RequestParam("keyword") String keyword, @RequestParam("sysId") Integer sysId) {
        logger.info("CALC recommend/get receive keyword:{}  sysId{}", keyword, sysId);
        List<QAVO> qaListSim = new ArrayList<>();
        if (StringUtil.isEmpty(keyword)) {
            return qaListSim;
        }
        HashMap map = new HashMap();
        map.put("sysId", sysId);
        Map<Integer, QAVO> qaList = contentService.findAllMap(map);
        //获取文档向量
        FixDocVectorModel docVectorModel = InitBaseVecModel.getDocVecModel(sysId, DocVectorType.Document);
        int count = 3;  //只需要三个最相似问题
        List<Map.Entry<Integer, Float>> analyList = docVectorModel.nearest10Doc(keyword);
        for (Map.Entry<Integer, Float> entry : analyList) {   //todo 可覆盖方法 默认是相近的10个文本
            //qaList序号entry.getKey()和相似度entry.getValue()
            if (count > 0 && entry.getValue() >= 0.6) { //todo set rate x1
                QAVO vo = qaList.get(entry.getKey());
                vo.setSimRate(entry.getValue());
                qaListSim.add(vo);
                count--;
            } else
                break;
        }
        //这里使用文本推荐而不是直接的相似度计算
        logger.info("CALC recommend/get operation end!");
        return qaListSim;
    }

}
