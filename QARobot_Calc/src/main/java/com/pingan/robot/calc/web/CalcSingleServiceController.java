package com.pingan.robot.calc.web;

import com.pingan.robot.calc.bean.DocVectorType;
import com.pingan.robot.calc.nlp.FixDocVectorModel;
import com.pingan.robot.calc.service.InitBaseVecModel;
import com.pingan.robot.calc.utils.CalcConstans;
import com.pingan.robot.common.log.PALogUtil;
import com.pingan.robot.common.utils.StringUtil;
import com.pingan.robot.common.vo.QAVO;
import com.pingan.robot.data.service.IContentService;
import com.pingan.robot.data.utils.ConstansDefination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sim")
public class CalcSingleServiceController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IContentService contentService;


    /**
     * 获取两个文本相似度
     */
    @CrossOrigin
    @RequestMapping(value = "/result")
    public float getSimResult(@RequestParam("ques1") String ques1, @RequestParam("ques2") String ques2) {
        logger.info("CALC /result receive ques1:{}  ques2:{}", ques1, ques2);
        FixDocVectorModel model = InitBaseVecModel.getDocVecModel(CalcConstans.SINGLE_MODE_SYSID, DocVectorType.Document);
        float sim = 0;
        if (model != null) {
            sim = model.similarity(ques1, ques2);
            sim = sim > 1 ? 1 : sim;
        }
        logger.info("CALC /result operation end, sim:{}", sim);
        return sim;
    }

    /**
     * 获取答案或相似问题
     */
    @CrossOrigin
    @RequestMapping(value = "/list")
    public List getSimList(@RequestParam("ques") String ques, @RequestParam("client") String client, @RequestParam("sysId") Integer sysId) {
        logger.info("CALC /list receive ques:{} sysId:{} client:{}", ques, sysId, client);
        List<QAVO> qaListSim = new ArrayList<>();
        FixDocVectorModel model = InitBaseVecModel.getDocVecModel(CalcConstans.SINGLE_MODE_SYSID, DocVectorType.Document);
        if (model == null) {
            logger.error("空文档向量模型初始化失败！");
            return null;
        }
        HashMap map = new HashMap();
        map.put("sysId", sysId);
        map.put("client", client);
        Map<Integer, QAVO> qaList = contentService.findAllMap(map);
        if (qaList == null || qaList.size() == 0)
            return qaListSim;
        int countValid = 0;
        for (Map.Entry<Integer, QAVO> entry : qaList.entrySet()) {
            if (StringUtil.isNotEmpty(entry.getValue().getQuestion())) {
                model.addDocument(entry.getKey(), entry.getValue().getQuestion());
                countValid++;
            }
        }
        List<Map.Entry<Integer, Float>> analyList = model.queryNearest(ques, countValid);
        for (Map.Entry<Integer, Float> entry : analyList) {
            QAVO vo = qaList.get(entry.getKey());
            vo.setSimRate(entry.getValue());
            qaListSim.add(vo);
        }
        model.removeAll();
        logger.info("CALC /list operation end");
        return qaListSim;
    }

    /**
     * 相似问题匹配检测
     */
    @CrossOrigin
    @RequestMapping(value = "/simCheck")
    public List simCheck(@RequestParam("client") String client, @RequestParam("sysId") Integer sysId) {
        String QUESTION = "Question", STANDARDQUES = "StandardQues", SIMLIST = "SimList";
        logger.info("CALC /simCheck receive sysId:{} client:{}", sysId, client);
        List<HashMap<String, Object>> checkResult = new ArrayList<>();
        FixDocVectorModel model = InitBaseVecModel.getDocVecModel(CalcConstans.SINGLE_MODE_SYSID, DocVectorType.Document);
        if (model == null) {
            logger.error("空文档向量模型初始化失败！");
            return null;
        }
        //standard qa
        HashMap map = new HashMap();
        map.put("sysId", sysId);
        map.put("client", client);
        Map<Integer, QAVO> qaList = contentService.findAllMap(map);
        if (qaList == null || qaList.size() == 0)
            return checkResult;
        //add standard qa-q to docVecModel
        for (Map.Entry<Integer, QAVO> entry : qaList.entrySet()) {
            if (StringUtil.isNotEmpty(entry.getValue().getQuestion())) {
                model.addDocument(entry.getKey(), entry.getValue().getQuestion());
            }
        }
        //test qa
        map.clear();
        map.put("sysId", sysId);
        map.put("client", client + "test");
        Map<Integer, QAVO> testList = contentService.findAllMap(map);
        if (testList == null || testList.size() == 0)
            return checkResult;
        //find sim doc
        int rightCount = 0;
        float avgRate = 0f;
        float lowRate = 1.1f;
        for (Map.Entry<Integer, QAVO> entry : testList.entrySet()) {
            HashMap<String, Object> checkMap = new HashMap<>(3);
            checkMap.put(QUESTION, entry.getValue().getQuestion());
            checkMap.put(STANDARDQUES, entry.getValue().getRemark());
            List<QAVO> sim = new ArrayList<>(5);
            List<Map.Entry<Integer, Float>> analyList = model.queryNearest(entry.getValue().getQuestion(), 3);
            int index = 0;
            for (Map.Entry<Integer, Float> ent : analyList) {
                QAVO base = qaList.get(ent.getKey());   //这个list中的对象不能更改
                QAVO vo = null;
                try {
                    vo = (QAVO) base.clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
                vo.setSimRate(ent.getValue());
                sim.add(vo);
                if (index == 0 && base.getQuestion().equals(entry.getValue().getRemark())) {
                    rightCount++;
                    avgRate += ent.getValue();
                    if (lowRate > ent.getValue())
                        lowRate = ent.getValue();
                    break;
                }
                index++;
            }
            checkMap.put(SIMLIST, sim);
            checkResult.add(checkMap);
        }
        HashMap<String, Object> countMap = new HashMap<>();
        countMap.put("testQuesCount", testList.size());
        countMap.put("standardQuesCount", qaList.size());
        countMap.put("rightCount", rightCount);
        float r = (float) rightCount / (float) testList.size();
        countMap.put("rightRate", String.valueOf(r));
        countMap.put("avgRate", avgRate / rightCount);
        countMap.put("lowRate", lowRate);
        checkResult.add(countMap);
        logger.info("CALC /simCheck operation end");
        return checkResult;
    }
}
