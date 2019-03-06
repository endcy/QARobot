package com.pingan.robot.calc.web;

import com.hankcs.hanlp.mining.word2vec.DocVectorModel;
import com.pingan.robot.calc.service.InitModel;
import com.pingan.robot.common.utils.StringUtil;
import com.pingan.robot.common.vo.QAVO;
import com.pingan.robot.data.dao.ICommonContDAO;
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
    private ICommonContDAO commonContDAO;

    /**
     * 获取答案或相似问题
     */
    @CrossOrigin
    @RequestMapping(value = "/get")
    public List<QAVO> getAnswer(@RequestParam("content") String content, @RequestParam("sysId") Integer sysId) {
        logger.info("CALC get receive question:{}  sysId:{}", content, sysId);
        List<QAVO> qaListSim = new ArrayList<>();
        if (StringUtil.isEmpty(content)) {
            return qaListSim;
        }
        HashMap map = new HashMap();
        map.put("sysId", sysId);
        List<QAVO> qaList = commonContDAO.findAll(map);
        DocVectorModel docVectorModel = InitModel.getDocModel();
        double maxRate = 0;
        QAVO target = null;
        try {
            for (QAVO vo : qaList) {
                float rate = docVectorModel.similarity(content, vo.getQuestion());
                vo.setSimRate(rate);
                if (rate >= 0.6) {        //todo set rate x
                    qaListSim.add(vo);
                    if (rate > maxRate) {
                        maxRate = rate;
                        target = vo;
                    }
                }
            }
            logger.info("question:{} 对比分析完成!", content);
        } catch (Exception e) {
            //PALogUtil.defaultErrorInfo(logger, e);
            e.printStackTrace();
            return qaListSim;
        }
        if (maxRate >= 0.82) {       //todo set rate y
            target.setRemark(ConstansDefination.STANDARD_ANSWER);
            qaListSim.clear();
            qaListSim.add(target);
        } else
            for (QAVO qavo : qaListSim)
                qavo.setAnswer(""); //推荐问题则清除答案
        qaListSim.sort(new Comparator<QAVO>() {
            @Override
            public int compare(QAVO o1, QAVO o2) {
                return o1.getSimRate() < o2.getSimRate() ? 1 : -1;
            }
        });
        if (qaListSim.size() > 3)
            qaListSim = qaListSim.subList(0, 3);
        return qaListSim;
    }


    @CrossOrigin
    @RequestMapping(value = "/recommend/get")
    public List<QAVO> getRecommend(@RequestParam("keyword") String keyword, @RequestParam("sysId") Integer sysId) {
        logger.info("CALC recommend receive keyword:{}  sysId", keyword, sysId);
        List<QAVO> qaListSim = new ArrayList<>();
        if (StringUtil.isEmpty(keyword)) {
            return qaListSim;
        }
        HashMap map = new HashMap();
        map.put("sysId", sysId);
        List<QAVO> qaList = commonContDAO.findAll(map);
        DocVectorModel docVectorModel = InitModel.getDocModel();
        for (int i = 0; i < qaList.size(); i++) {
            docVectorModel.addDocument(i, qaList.get(i).getQuestion());
        }
        int count = 3;  //只需要三个最相似问题
        for (Map.Entry<Integer, Float> entry : docVectorModel.nearest(keyword)) {   //todo 可覆盖方法 默认是相近的10个文本
            //qaList序号entry.getKey()和相似度entry.getValue()
            if (count > 0 && entry.getValue() >= 0.6) { //todo  set rate x1
                QAVO vo = qaList.get(entry.getKey());
                vo.setSimRate(entry.getValue());
                vo.setAnswer("");   //推荐的消息对象清除答案
                qaListSim.add(vo);
                count--;
            } else
                break;
        }
        //这里使用文本推荐而不是直接的相似度计算
        return qaListSim;
    }

}
