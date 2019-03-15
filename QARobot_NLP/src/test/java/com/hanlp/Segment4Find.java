package com.hanlp;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.NotionalTokenizer;
import com.pingan.robot.RobotNLPApplication;
import com.pingan.robot.common.vo.QAVO;
import com.pingan.robot.data.dao.ICommonContDAO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;

/**
 * @Description
 * @Author chenxx
 * @Date 2019/3/6 15:00
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RobotNLPApplication.class)
public class Segment4Find {
    @Autowired
    private ICommonContDAO commonContDAO;

    @Test
    public void getAllSegment() throws Exception {
        HashMap map = new HashMap();
        map.put("sysId", 1);
        map.put("client", "中国太平test");
        List<QAVO> qaList = commonContDAO.findAll(map);
        System.out.println("size:" + qaList.size());
//        String[] segTypes = new String[]{"crf", "viterbi", "dat", "nshort"};
        String[] segTypes = new String[]{"dat"};
        for (String segType : segTypes) {
            printfSeg(qaList, segType);
        }
    }

    private synchronized void printfSeg(List<QAVO> qaList, String segType) throws Exception {
        Segment segment = HanLP.newSegment(segType);
        NotionalTokenizer.SEGMENT = segment;
        segType = segType + "-cutStopWords-Fix";
        String fileName = "C:\\Users\\admin\\Desktop\\NLP\\segTest\\" + segType + ".txt";
        File file = new File(fileName);
        if (!file.exists()) file.createNewFile();
        FileOutputStream outputStream = new FileOutputStream(file);
        StringBuffer buffer = new StringBuffer();
        for (QAVO vo : qaList) {
            List<Term> termList = NotionalTokenizer.segment(vo.getQuestion());
//            List<Term> termList = segment.seg(vo.getQuestion());
            buffer.append(vo.getQuestion()).append("\n");
            for (Term term : termList) {
                buffer.append(term.word).append("\t");
                System.out.print(term.word + "\t");
            }
            buffer.append("\n");
            System.out.println("\n*********");
        }
        outputStream.write(buffer.toString().getBytes("utf8"));
    }
}
