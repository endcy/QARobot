package com.dao;

import com.pingan.robot.RobotCalcApplication;
import com.pingan.robot.calc.service.MarkRightAnsService;
import com.pingan.robot.common.vo.QAVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Description
 * @Author chenxx
 * @Date 2019/3/20 10:09
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RobotCalcApplication.class)
public class UpdateContent {
    @Autowired
    private MarkRightAnsService markService;

    @Test
    public void udpKy() {
        QAVO qavo = new QAVO();
        qavo.setId(117);
        qavo.setKeyword("123");
        markService.markIdxInKeyWord(qavo, 1);
        System.out.println("");
    }
}
