package com.pingan.robot.data.service;

import com.pingan.robot.common.vo.QAVO;

public interface IContentService {

    QAVO segment(QAVO qaVO);

    QAVO sentiment(QAVO qaVO);

    QAVO polarity(QAVO qaVO);

    QAVO find(String id, Integer sysId);

}
