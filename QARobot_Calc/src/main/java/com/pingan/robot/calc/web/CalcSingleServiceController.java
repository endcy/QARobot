package com.pingan.robot.calc.web;

import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.NotionalTokenizer;
import com.pingan.robot.calc.bean.DocVectorType;
import com.pingan.robot.calc.bean.SimpleAnsVO;
import com.pingan.robot.calc.nlp.FixDocVectorModel;
import com.pingan.robot.calc.nlp.FixWordVectorModel;
import com.pingan.robot.calc.service.InitBaseVecModel;
import com.pingan.robot.calc.service.InitVecModelService;
import com.pingan.robot.calc.service.MarkRightAnsService;
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

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/sim")
public class CalcSingleServiceController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IContentService contentService;

    @Autowired
    private MarkRightAnsService markAnsService;

    @Resource
    private InitVecModelService vecModelService;

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
        model.removeAll();
        HashMap map = new HashMap();
        map.put("sysId", sysId);
        map.put("client", client);
        Map<Integer, QAVO> qaList = contentService.findAllMap(map);
        if (qaList == null || qaList.size() == 0)
            return qaListSim;
        int countValid = qaList.size();
        for (Map.Entry<Integer, QAVO> entry : qaList.entrySet()) {
            if (StringUtil.isNotEmpty(entry.getValue().getQuestion())) {
                model.addDocument(entry.getKey(), entry.getValue().getQuestion());
            }
        }
        List<Map.Entry<Integer, Float>> analyList = model.queryNearest(ques, 3 > countValid ? countValid : 3);
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
        model.removeAll();
        //standard qa
        HashMap map = new HashMap();
        map.put("sysId", sysId);
        map.put("client", client);
        Map<Integer, QAVO> qaList = contentService.findAllMap(map);
        if (qaList == null || qaList.size() == 0)
            return checkResult;
        int qaStandardCount = qaList.size();
        int simSize = qaStandardCount;
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
        int rightCount[] = new int[]{0, 0, 0};
        float avgRate[] = new float[]{0f, 0f, 0f};
        float lowRate[] = new float[]{1.1f, 1.1f, 1.1f};
        for (Map.Entry<Integer, QAVO> entry : testList.entrySet()) {
            HashMap<String, Object> checkMap = new HashMap<>(3);
            checkMap.put(QUESTION, entry.getValue().getQuestion());
            checkMap.put(STANDARDQUES, entry.getValue().getRemark());
            List<SimpleAnsVO> sim = new ArrayList<>(5);
            List<Map.Entry<Integer, Float>> analyList = model.queryNearest(entry.getValue().getQuestion(), simSize);
            int index = 1;
            for (Map.Entry<Integer, Float> ent : analyList) {
                QAVO base = qaList.get(ent.getKey());   //这个list中的对象不能更改
                //只有在匹配前三才列出来
                if (index < 4) {
                    QAVO vo = null;
                    try {
                        vo = (QAVO) base.clone();
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                    vo.setSimRate(ent.getValue());
                    SimpleAnsVO simpleAnsVO = new SimpleAnsVO(vo);
                    sim.add(simpleAnsVO);
                }
                //遇到了等于标准问题则不再循环判断并记录index
                if (base.getQuestion().equals(entry.getValue().getRemark())) {
                    switch (index) {
                        case 1:
                        case 2:
                        case 3:
                            rightCount[index - 1]++;
                            avgRate[index - 1] += ent.getValue();
                            if (lowRate[index - 1] > ent.getValue())
                                lowRate[index - 1] = ent.getValue();
                            break;
                        default:
                            break;
                    }
                    break;
                }
                index++;
            }
            //mark stdQues index
            markAnsService.markIdxInKeyWord(entry.getValue(), index);
            checkMap.put(SIMLIST, sim);
            checkResult.add(checkMap);
        }
        //包装统计数据
        HashMap<String, Object> countMap = new HashMap<>();
        countMap.put("testQuesCount", testList.size());
        countMap.put("standardQuesCount", qaStandardCount);
        countMap.put("rightCount1", rightCount[0]);
        countMap.put("rightCount2", rightCount[1]);
        countMap.put("rightCount3", rightCount[2]);
        float x = (float) testList.size();
        float r1 = (float) rightCount[0] / x;
        float r2 = (float) rightCount[1] / x;
        float r3 = (float) rightCount[2] / x;
        float mr = (float) (x - rightCount[0] - rightCount[1] - rightCount[2]) / x;
        countMap.put("rightRate1", String.valueOf(r1));
        countMap.put("rightRate2", String.valueOf(r2));
        countMap.put("rightRate3", String.valueOf(r3));
        countMap.put("avgRate1", avgRate[0] / rightCount[0]);
        countMap.put("avgRate2", avgRate[1] / rightCount[1]);
        countMap.put("avgRate3", avgRate[2] / rightCount[2]);
        countMap.put("lowRate1", lowRate[0]);
        countMap.put("lowRate2", lowRate[1]);
        countMap.put("lowRate3", lowRate[2]);
        countMap.put("missRate", String.valueOf(mr));
        checkResult.add(countMap);
        model.removeAll();
        logger.info("CALC /simCheck operation end");
        return checkResult;
    }

    /**
     * 相似问题匹配检测
     */
    @CrossOrigin
    @RequestMapping(value = "/simSeg")
    public String simCheck() {
        try {
            List<HashMap<String, Object>> checkResult = new ArrayList<>();
            FixDocVectorModel model = InitBaseVecModel.getDocVecModel(CalcConstans.SINGLE_MODE_SYSID, DocVectorType.Document);
            if (model == null) {
                return "error";
            }
            String clients[] = new String[]{"中国太平", "中美联泰大都会人寿", "华夏人寿", "生命人寿", "中国平安"};
            for (String client : clients) {
                String fileName = "C:\\Users\\admin\\Desktop\\NLP\\simSegTest\\" + client + ".txt";
                File file = new File(fileName);
                if (!file.exists()) file.createNewFile();
                FileOutputStream outputStream = new FileOutputStream(file);
                StringBuffer buffer = new StringBuffer();
                model.removeAll();
                //standard qa
                HashMap map = new HashMap();
                map.put("sysId", 1);
                map.put("client", client);
                Map<Integer, QAVO> qaList = contentService.findAllMap(map);
                int indexCount = qaList.size();
                if (qaList == null || qaList.size() == 0)
                    return "error";
                for (Map.Entry<Integer, QAVO> entry : qaList.entrySet()) {
                    String ques = entry.getValue().getQuestion();
                    if (StringUtil.isNotEmpty(ques)) {
                        ques = replaceBlank(ques);
                        List<Term> termList = NotionalTokenizer.segment(ques);
                        buffer.append(entry.getKey()).append(":").append(printTermList(ques, termList));
                        model.addDocument(entry.getKey(), ques);
                    }
                }
                buffer.append("\n");
                outputStream.write(buffer.toString().getBytes("utf8"));
                outputStream.flush();
                buffer.setLength(0);
                buffer.append("#####################################\n");
                //test qa
                Map<Integer, QAVO> testList = new HashMap<>();
                //从2开始
                for (int i = 2; i < indexCount + 1; i++) {
                    map.clear();
                    map.put("sysId", 1);
                    map.put("keyword", i);
                    map.put("client", client + "test");
                    Map<Integer, QAVO> temp = contentService.findAllMap(map);
                    testList.putAll(temp);
                }
                for (Map.Entry<Integer, QAVO> entry : testList.entrySet()) {
                    String ques = entry.getValue().getQuestion();
                    String tarQues = entry.getValue().getRemark();
                    if (StringUtil.isNotEmpty(ques)) {
                        ques = replaceBlank(ques);
                        List<Term> termList = NotionalTokenizer.segment(ques);
                        buffer.append("SimIdx|Question|Words\t").append(entry.getValue().getKeyword()).append("\t").append(printTermList(ques, termList));
                    }
                    if (StringUtil.isNotEmpty(tarQues)) {
                        tarQues = replaceBlank(tarQues);
                        List<Term> termList = NotionalTokenizer.segment(tarQues);
                        buffer.append("TargetQs|Words\t").append(printTermList(tarQues, termList));
                    }
                    buffer.append("\n");
                }

                buffer.append("#####################################");
                outputStream.write(buffer.toString().getBytes("utf8"));
                outputStream.flush();
                outputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ok";
    }

    /**
     * 相似问题匹配检测
     */
    @CrossOrigin
    @RequestMapping(value = "/refreshVec")
    public String refreshCusSynoVec() {
        vecModelService.refreshCustomSynoVec();
        return "ok";
    }

    private String printTermList(String content, List<Term> termList) {
        StringBuffer buffer = new StringBuffer(content + "\t" + termList.size() + ": \n");
        FixWordVectorModel fixWordVectorModel = InitBaseVecModel.getWordVecModel(999);
        for (Term term : termList) {
            if (fixWordVectorModel.vector(term.word) == null)
                buffer.append("**");
            buffer.append(term.word).append("\t");
        }
        buffer.append("\n");
        return buffer.toString();
    }

    private String replaceBlank(String content) {
        Pattern p = Pattern.compile("\\s*|\t|\r|\n");
        Matcher m = p.matcher(content);
        content = m.replaceAll("");
        return content;
    }

}
