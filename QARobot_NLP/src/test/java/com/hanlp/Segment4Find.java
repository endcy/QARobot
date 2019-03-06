package com.hanlp;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import com.pingan.robot.RobotNLPApplication;
import com.pingan.robot.common.vo.QAVO;
import com.pingan.robot.data.dao.ICommonContDAO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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
    public void getAllSegment() {
        HashMap map = new HashMap();
        map.put("sysId", 0);
        List<QAVO> qaList = commonContDAO.findAll(map);
        System.out.println("size:" + qaList.size());
        Segment segment = HanLP.newSegment("crf");
        for (QAVO vo : qaList) {
            List<Term> termList = segment.seg(vo.getQuestion() + "?" + vo.getAnswer());
            for (Term term : termList) {
                System.out.print(term.word + "\t");
            }
            System.out.println("*********");
        }
    }

    @Test
    public void getAllSegment2() {
        HashMap map = new HashMap();
        map.put("sysId", 0);
        List<QAVO> qaList = commonContDAO.findAll(map);
        System.out.println("size:" + qaList.size());
        Segment segment = HanLP.newSegment();
        for (QAVO vo : qaList) {
            List<Term> termList = segment.seg(vo.getQuestion() + "?" + vo.getAnswer());
            for (Term term : termList) {
                System.out.print(term.word + "\t");
            }
            System.out.println("*********");
        }
    }
}
