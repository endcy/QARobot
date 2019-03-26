package com.service;

import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.NotionalTokenizer;
import com.pingan.robot.RobotCalcApplication;
import com.pingan.robot.calc.bean.DocVectorType;
import com.pingan.robot.calc.nlp.FixDocVectorModel;
import com.pingan.robot.calc.service.InitBaseVecModel;
import com.pingan.robot.calc.utils.CalcConstans;
import com.pingan.robot.common.utils.StringUtil;
import com.pingan.robot.common.vo.QAVO;
import com.pingan.robot.data.service.IContentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description
 * @Author chenxx
 * @Date 2019/3/21 19:08
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RobotCalcApplication.class)
public class QuesSegAnalysis {

    @Autowired
    private IContentService contentService;

    @Test
    public void segAllQues() throws Exception {
        List<HashMap<String, Object>> checkResult = new ArrayList<>();
        FixDocVectorModel model = InitBaseVecModel.getDocVecModel(CalcConstans.SINGLE_MODE_SYSID, DocVectorType.Document);
        if (model == null) {
            return;
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
                return;
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
            buffer.append("#####################################");
            //test qa
            Map<Integer, QAVO> testList = new HashMap<>();
            //从2开始
            for (int i = 2; i < indexCount + 1; i++) {
                map.clear();
                map.put("sysId", 1);
                map.put("remark", i);
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
                    buffer.append("Question\t").append(printTermList(ques, termList));
                }
                if (StringUtil.isNotEmpty(tarQues)) {
                    tarQues = replaceBlank(tarQues);
                    List<Term> termList = NotionalTokenizer.segment(tarQues);
                    buffer.append("TargetQs\t").append(printTermList(tarQues, termList));
                }
                buffer.append("\n");
            }

            buffer.append("#####################################");
            outputStream.write(buffer.toString().getBytes("utf8"));
            outputStream.flush();
            outputStream.close();
        }
    }

    public String printTermList(String content, List<Term> termList) {
        StringBuffer buffer = new StringBuffer(content + " :" + termList.size() + ": \n");
        for (Term term : termList) {
            buffer.append(term.word).append("\t");
        }
        buffer.append("\n");
        return buffer.toString();
    }

    public String replaceBlank(String content) {
        Pattern p = Pattern.compile("\\s*|\t|\r|\n");
        Matcher m = p.matcher(content);
        content = m.replaceAll(" ");
        return content;
    }

}