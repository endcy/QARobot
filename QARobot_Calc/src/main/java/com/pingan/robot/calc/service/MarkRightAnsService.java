package com.pingan.robot.calc.service;

import com.pingan.robot.common.log.PALogUtil;
import com.pingan.robot.common.vo.QAVO;
import com.pingan.robot.data.dao.ICommonContDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description
 * @Author chenxx
 * @Date 2019/3/19 18:07
 **/
@Component
public class MarkRightAnsService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private ICommonContDAO commonContDAO;

    /**
     * 在keyword位置标记答案是第几位
     *
     * @param vo
     * @param index
     */
    public void markIdxInKeyWord(QAVO vo, int index) {
        QAVO mark = new QAVO();
        mark.setId(vo.getId());
        mark.setKeyword(String.valueOf(index));
        try {
            commonContDAO.update(mark);
        } catch (Exception e) {
            PALogUtil.defaultErrorInfo(logger, e);
            e.printStackTrace();
        }

    }

}
