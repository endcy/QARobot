package com.pingan.robot.calc.bean;

import com.pingan.robot.common.vo.BaseVO;
import com.pingan.robot.common.vo.QAVO;

import java.io.Serializable;

/**
 * @Description 返回简单的数据展示
 * @Author chenxx
 * @Date 2019/3/21 17:11
 **/
public class SimpleAnsVO extends BaseVO implements Serializable, Cloneable {
    private static final long serialVersionUID = -7888786109761308867L;
    private Float simRate;

    public SimpleAnsVO(QAVO qavo) {
        setId(qavo.getId());
        setSysId(qavo.getSysId());
        setQuestion(qavo.getQuestion());
        setAnswer(qavo.getAnswer());
        this.simRate = qavo.getSimRate();
    }
}
