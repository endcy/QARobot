package com.pingan.robot.calc.utils;

import com.pingan.robot.common.vo.QAVO;

import java.util.Comparator;

/**
 * @Description
 * @Author chenxx
 * @Date 2019/3/7 15:00
 **/
public class QavoSimComparator implements Comparator<QAVO> {

    public int compare(QAVO o1, QAVO o2) {
        return o1.getSimRate() < o2.getSimRate() ? 1 : -1;
    }

}
